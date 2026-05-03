# Booking Mikroservis (Rezervacije)

## Tehničke specifikacije

| | |
|---|---|
| **Framework** | Spring Boot 3.5.11 |
| **Jezik** | Java 21 |
| **Baza podataka** | H2 (in-memory) |
| **Port** | `8083` |
| **Dokumentacija** | Swagger UI kroz Gateway: `http://localhost:8080/swagger-ui.html` |
| **H2 konzola** | `http://localhost:8083/h2-console` |
| **Config Server** | `http://localhost:8888` (opciono) |

---

## Opis poslovne logike

Booking mikroservis upravlja celim životnim ciklusom rezervacija smeštajnih kapaciteta u eko-turističkom sistemu. Servis omogućava:

- Kreiranje rezervacija uz automatsko postavljanje statusa na `PENDING`
- Izračunavanje ukupne cene boravka na osnovu podataka iz Smeštaj servisa: `(basePrice + Σ facilityCosts) × brojNoći`
- Upravljanje životnim ciklusom rezervacije: `PENDING → CONFIRMED → COMPLETED` ili `CANCELLED`
- Automatsko logovanje svake promene statusa u `BookingStatusLog`
- Kalkulaciju iznosa povrata novca pri otkazivanju putem Strategy obrasca na osnovu politike otkazivanja
- Upravljanje gostima i dodatnim stavkama (add-on items) vezanim za rezervaciju
- Dinamičku konfiguraciju welcome poruke putem `@RefreshScope`
- Inter-servisnu komunikaciju sa Smeštaj servisom putem Feign klijenta
- Zaštitu od pada zavisnih servisa putem Circuit Breaker mehanizma
- Zaštitu od preopterećenja putem Rate Limiter mehanizma

---

## Arhitekturni obrasci

### Strategy (Politika povrata novca)

Implementiran u paketu `strategy` za izračunavanje iznosa povrata pri otkazivanju rezervacije. `BookingService.calculateCancellationRefund(bookingId)` delegira na `CancellationStrategyResolver.resolve(policy)` koji čita `refundPercentage` iz politike otkazivanja i vraća odgovarajuću strategiju.

```
CancellationRefundStrategy (interfejs)
    ├── FullRefundStrategy    → totalPrice          (refundPercentage >= 100)
    ├── PartialRefundStrategy → totalPrice × (refundPercentage / 100.0)
    └── NoRefundStrategy      → 0.0                 (refundPercentage <= 0)

CancellationStrategyResolver.resolve(policy) → bira strategiju
```

**Zašto je Strategy dobar fit:** Eko-turizam ima varijabilne politike otkazivanja — udio povrata zavisi od tipa smeštaja, sezone i roka do dolaska. Strategy odvaja algoritam kalkulacije od `BookingService`, koji ne zna niti treba da zna koji se algoritam primjenjuje. `CancellationStrategyResolver` centralizuje tu odluku — dodavanje nove politike (npr. tiered refund koji ovisno o broju dana do dolaska vraća različit udio) zahtijeva samo novu klasu, bez izmjene servisa.

---

## Inter-servisna komunikacija

### Feign klijenti

**UnitClient** — komunikacija sa Smeštaj servisom (port 8081):

| Metoda | Endpoint | Opis |
|---|---|---|
| `getUnitById(id)` | GET /api/unit/{id} | Dohvata `basePrice` i listu `facilities` za izračun `totalPrice` rezervacije |

### Resilience mehanizmi

- **Circuit Breaker** (`smestajServiceCB`) — otvara se nakon 50% grešaka u prozoru od 10 poziva, čeka 10s, testira 3 poziva u polu-otvorenom stanju
- **Rate Limiter** (`basicLimit`) — maksimalno 5 zahtjeva na 10 sekundi, timeout 100ms
- **Fallback metode** — razlikuje `ResourceNotFoundException` (HTTP 404) od stvarnog pada servisa (HTTP 503)

### Distribuirano praćenje (Trace ID)

`CorrelationIdFilter` automatski prima `X-Trace-Id` header i upisuje ga u MDC. `FeignClientInterceptor` propagira tri headera na svaki izlazni Feign poziv: `X-Trace-Id`, `X-Authenticated-User`, `X-User-Role`.

Format logova:
```
2026-04-14 10:15:30 [http-nio-8083-exec-1] INFO [traceId=abc123] ...
```

---

## Validacije pri kreiranju rezervacije

- `@NotNull` — `unitId`, `guestId`, `cancellationPolicyId` (obavezna polja)
- `@FutureOrPresent` — `startDate` — "Datum početka mora biti danas ili u budućnosti"
- `@Future` — `endDate` — "Datum kraja mora biti u budućnosti"
- Status se automatski postavlja na `PENDING` — klijent ne može proslijediti status
- Pri promjeni statusa: `CANCELLED` rezervacija ne može se dalje mijenjati

---

## Implementirani endpointi

### Rezervacije — `/api/bookings`

1. `GET` "/api/bookings/api/v5/orders/welcome"
   Vraća konfigurabilnu poruku dobrodošlice
   Napomena: `@RefreshScope` — poruka se dinamički čita iz Config Servera (`custom.welcome-message`), osvežava se bez restarta

2. `GET` "/api/bookings"
   Dohvatanje svih rezervacija
   Parametri:
   - unitId — Long, nije obavezan, filter po ID-u smeštajne jedinice
   - date — LocalDate, nije obavezan, vraća rezervacije aktivne na taj datum (format: yyyy-MM-dd)

3. `GET` "/api/bookings/{id}"
   Dohvatanje rezervacije po ID-u

4. `POST` "/api/bookings"
   Kreiranje nove rezervacije — matematička kalkulacija + poziv Smeštaj servisa
   Parametri: unitId (@NotNull), guestId (@NotNull), cancellationPolicyId (@NotNull), startDate (@FutureOrPresent), endDate (@Future)
   Formula: totalPrice = (basePrice + Σ facilityCosts) × ChronoUnit.DAYS.between(startDate, endDate)
   Napomena: Rate Limiter zaštita — max 5 zahtjeva na 10 sekundi, Circuit Breaker zaštita — smestajServiceCB

5. `PUT` "/api/bookings/{id}"
   Izmjena detalja rezervacije (unitId, startDate, endDate, cancellationPolicyId, guestId)

6. `PATCH` "/api/bookings/{id}/status"
   Promjena statusa rezervacije
   Parametri:
   - status — String, obavezan, novi status

   Dozvoljeni prelazi:
   - PENDING → CONFIRMED
   - PENDING → CANCELLED
   - CONFIRMED → COMPLETED
   - CONFIRMED → CANCELLED

   Nedozvoljeni prelazi:
   - CANCELLED → bilo šta

   Napomena: svaka promjena automatski kreira zapis u `BookingStatusLog`

7. `GET` "/api/bookings/{id}/nights"
   Matematička kalkulacija — broj noći boravka
   Formula: `ChronoUnit.DAYS.between(startDate, endDate)`

8. `GET` "/api/bookings/{id}/refund"
   Matematička kalkulacija — iznos povrata novca pri otkazivanju putem Strategy obrasca
   Logika: `CancellationStrategyResolver.resolve(booking.getCancellationPolicy()).calculateRefund(booking.getTotalPrice())`

---

### Gosti — `/api/guests`

1. `GET` "/api/guests"
   Dohvatanje svih gostiju
   Parametri:
   - email — String, nije obavezan, filter po e-mail adresi

2. `GET` "/api/guests/{id}"
   Dohvatanje gosta po ID-u

3. `POST` "/api/guests"
   Kreiranje novog gosta

4. `PUT` "/api/guests/{id}"
   Izmjena podataka gosta

---

### Dodatne stavke — `/api/add-on-items`

1. `GET` "/api/add-on-items/booking/{bookingId}"
   Sve dodatne stavke za rezervaciju

2. `GET` "/api/add-on-items/{id}"
   Dohvatanje dodatne stavke po ID-u

3. `POST` "/api/add-on-items/booking/{bookingId}"
   Kreiranje nove dodatne stavke za rezervaciju
   Parametri: bookingId u putanji + tijelo zahtjeva

4. `PUT` "/api/add-on-items/{id}"
   Izmjena dodatne stavke

5. `DELETE` "/api/add-on-items/{id}"
   Brisanje dodatne stavke

6. `GET` "/api/add-on-items/booking/{bookingId}/total"
   Matematička kalkulacija — ukupna cijena dodatnih stavki
   Formula: Σ price svih add-on stavki za rezervaciju

---

### Politike otkazivanja — `/api/cancellation-policies`

1. `GET` "/api/cancellation-policies"
   Dohvatanje svih politika otkazivanja

2. `GET` "/api/cancellation-policies/{id}"
   Dohvatanje politike po ID-u

3. `POST` "/api/cancellation-policies"
   Kreiranje nove politike otkazivanja
   Parametri: policyName (@NotBlank), refundPercentage (@NotNull, @Min(0), @Max(100))

4. `PUT` "/api/cancellation-policies/{id}"
   Izmjena politike otkazivanja

5. `DELETE` "/api/cancellation-policies/{id}"
   Brisanje politike otkazivanja

---

### Log promena statusa — `/api/booking-status-logs`

1. `GET` "/api/booking-status-logs"
   Dohvatanje svih zapisa promjena statusa

2. `GET` "/api/booking-status-logs/booking/{bookingId}"
   Istorija promjena statusa za konkretnu rezervaciju (sortirano od najnovijeg)

---

## Obrada grešaka

`FeignErrorDecoder` presreće HTTP greške iz zavisnih servisa i mapira ih u čitljive poruke:
- 400 → BadRequestException
- 401 → greška autentifikacije između servisa
- 403 → zabranjen pristup resursu
- 404 → ResourceNotFoundException (resurs nije pronađen)
- 429 → servis preopterećen (Rate Limiter na ciljnom servisu)

`GlobalExceptionHandler` sve greške vraća u uniformnom formatu:
```json
{
  "status": 404,
  "message": "Rezervacija sa ID-em 3 nije pronađena",
  "timestamp": "2026-04-14T10:15:30",
  "traceId": "abc123"
}
```

---

## Lokalno pokretanje

### Preduslovi
- Java 21
- Maven 3.6+

### Redosled pokretanja celog sistema

```
1. config-service     (port 8888)  ← prvi obavezno
2. auth-service       (port 8085)
3. smestaj-service    (port 8081)  ← booking-service zavisi od ovog
4. booking-service    (port 8083)  ← ovaj servis
5. finansije-service  (port 8084)
6. gateway-service    (port 8080)  ← poslednji obavezno
```

### Pokretanje

```bash
cd booking-service
./mvnw spring-boot:run
```

### Testiranje putem Swagger UI

Preporučeno — kroz Gateway (sa autentifikacijom):
```
http://localhost:8080/swagger-ui.html
```
Odaberi "Rezervacije Service" iz padajućeg menija i dodaj X-API-KEY u "Authorize".

Direktan pristup (bez Gateway autentifikacije):
```
http://localhost:8083/swagger-ui/index.html
```

### API ključevi za testiranje

| Korisnik | API ključ | Uloga | Prava |
|---|---|---|---|
| admin_user | admin-key-123 | ADMIN | GET, POST, PUT, DELETE |
| guide_user | guide-key-789 | GUIDE | GET, POST, PUT |
| guest_user | guest-key-456 | GUEST | GET |

### Monitoring Circuit Breaker-a

```
http://localhost:8083/actuator/health
```

Prikazuje stanje Circuit Breaker-a (`smestajServiceCB`):
- `CLOSED` — servis radi normalno
- `OPEN` — servis je pao, zahtjevi se odbijaju odmah
- `HALF_OPEN` — testiranje da li je servis ponovo dostupan

### Testiranje putem H2 konzole

```
URL:      http://localhost:8083/h2-console
JDBC URL: jdbc:h2:mem:orderdb
Username: sa
Password: (prazno)
```

**Inicijalni podaci (data.sql):**
- 5 gostiju
- 3 politike otkazivanja (100%, 50%, 0% povrata)
- 6 rezervacija u različitim statusima
- 7 dodatnih stavki
- 9 zapisa promjena statusa

---

## Konfiguracija (application.properties)

```properties
spring.application.name=rezervacije-service
spring.config.import=optional:configserver:http://localhost:8888

server.port=8083

spring.datasource.url=jdbc:h2:mem:orderdb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=none
spring.sql.init.mode=always
spring.jpa.defer-datasource-initialization=true

# Circuit Breaker
resilience4j.circuitbreaker.instances.smestajServiceCB.register-health-indicator=true
resilience4j.circuitbreaker.instances.smestajServiceCB.sliding-window-size=10
resilience4j.circuitbreaker.instances.smestajServiceCB.failure-rate-threshold=50
resilience4j.circuitbreaker.instances.smestajServiceCB.wait-duration-in-open-state=10s
resilience4j.circuitbreaker.instances.smestajServiceCB.permitted-number-of-calls-in-half-open-state=3

# Rate Limiter
resilience4j.ratelimiter.instances.basicLimit.limit-for-period=5
resilience4j.ratelimiter.instances.basicLimit.limit-refresh-period=10s
resilience4j.ratelimiter.instances.basicLimit.timeout-duration=100ms

# Config Server dinamička konfiguracija
custom.welcome-message=Dobrodosli u Order sistem v6!

# Actuator
management.endpoints.web.exposure.include=health,info,refresh
management.endpoint.health.show-details=always
```
