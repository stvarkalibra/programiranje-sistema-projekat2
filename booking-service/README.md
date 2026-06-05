# Booking Mikroservis (Rezervacije)

## Tehničke specifikacije

| | |
|---|---|
| **Framework** | Spring Boot 3.5.11 |
| **Jezik** | Java 21 |
| **Baza podataka** | MySQL 8.0 (Docker kontejner `db-mysql`, baza `eco_tourism_db`) |
| **Port** | `8083` |
| **Skaliranje** | 3 Docker replike (`deploy.replicas: 3`) |
| **Servisni registar** | Eureka (`http://discovery-service:8761`) |
| **Dokumentacija** | Swagger UI kroz Gateway: `http://localhost:8080/swagger-ui.html` |
| **Config Server** | `http://localhost:8888` |
| **Logovi** | Konzola + Loki (`http://loki:3100`) |
| **Tracing** | Zipkin (`http://zipkin:9411`), W3C `traceparent` format |

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

API Gateway generiše `traceparent` header u W3C formatu (`00-{traceId}-{spanId}-01`) i propagira ga kroz celu mrežu. Svaki mikroservis nasljeđuje `traceId` i upisuje ga u sve logove putem MDC-a.

Format konzolnih logova:
```
2026-04-14 10:15:30 [http-nio-8083-exec-1] INFO  [rezervacije-service] [traceId=abc123def456] o.e.r.s.BookingService - Rezervacija kreirana
```

---

## Asinhrona komunikacija (RabbitMQ)

Booking servis učestvuje u Event-Driven arhitekturi kroz RabbitMQ broker. Sve izmene stanja koje imaju poslovni uticaj na ostatak sistema objavljuju se kao poruke — servis ne čeka odgovor i ostaje potpuno operativan čak i kada su zavisni servisi nedostupni.

### Konfiguracija redova i razmenjivača

| Red | Razmenjivač | Routing ključ | Smer | Uloga |
|-----|------------|---------------|------|-------|
| `transaction_queue` | `transaction_exchange` | `transaction_routing_key` | Šalje → Finansije | Finansijski događaji pri promeni statusa rezervacije |
| `availability_queue` | `availability_exchange` | `availability_routing_key` | Šalje → Avantura | Obaveštava o promeni dostupnosti (otkazivanje) |
| `booking_feedback_queue` | `booking_feedback_exchange` | `booking_feedback_key` | Prima ← Finansije | Povratna informacija o uspešnoj obradi transakcije |
| `booking_time_slot_deleted_queue` | `time_slot_deleted_exchange` | `time.slot.deleted` | Prima ← Avantura | Brisanje termina — automatski otkazuje rezervacije |

### Događaji koje servis objavljuje

| Operacija | Red | Sadržaj poruke |
|-----------|-----|----------------|
| `PATCH /api/bookings/{id}/status` → CONFIRMED | `transaction_exchange` | `bookingId`, `totalPrice`, `guestId` |
| `PATCH /api/bookings/{id}/status` → CANCELLED | `availability_exchange` | `unitId`, `startDate`, `endDate` |
| `DELETE /api/add-on-items/{id}` | `availability_exchange` | `bookingId`, tip promene |

### Otpornost na greške

- Poruke koje nisu isporučene ostaju u redu sve dok potrošač ne postane dostupan — nema gubitka podataka
- Booking servis ne blokira HTTP zahtev čekanjem na potvrdu od Finansije servisa
- `booking_feedback_queue` je trajan (`durable=true`) — preživljava restart brokera
- Demonstracija: terminacijom `finances-service` kontejnera poruke se akumuliraju u redu i automatski obrađuju pri ponovnom pokretanju

### Tok poruke (primer: potvrda rezervacije)

```
POST /api/bookings/{id}/status?status=CONFIRMED
        ↓
BookingService.updateStatus()
        ↓
RabbitTemplate.convertAndSend("transaction_exchange", "transaction_routing_key", event)
        ↓
[RabbitMQ broker — transaction_queue]
        ↓
finances-service: TransactionEventListener.onTransactionEvent()
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

## Pokretanje sistema (Docker Compose)

Ceo sistem se podiže jednom komandom iz korena projekta:

```bash
./start.sh
```

Skripta automatski:
1. Briše prethodno pokrenute kontejnere i Docker slike (`docker compose down --remove-orphans`)
2. Gradi i pokreće sve servise (`docker compose up --build -d`)
3. Čeka 35 sekundi na stabilizaciju
4. Čisti RabbitMQ redove od prethodnih sesija
5. Prikazuje status svih kontejnera i link-ove za pristup

### Pristupne tačke

| Servis | URL | Pristup |
|--------|-----|---------|
| API Gateway | `http://localhost:8080` | svi zahtevi kroz ovde |
| Swagger UI | `http://localhost:8080/swagger-ui.html` | odaberi "Rezervacije Service" |
| Eureka Dashboard | `http://localhost:8761` | prikaz instanci booking-service |
| Grafana | `http://localhost:3000` | admin / admin |
| Zipkin | `http://localhost:9411` | pretraga tragova |
| RabbitMQ UI | `http://localhost:15672` | guest / guest |
| Adminer (DB) | `http://localhost:8085` | pregled MySQL tabela |

### API ključevi za testiranje

| Korisnik | API ključ | Uloga | Prava |
|---|---|---|---|
| admin_user | admin-key-123 | ADMIN | GET, POST, PUT, DELETE |
| guide_user | guide-key-789 | GUIDE | GET, POST, PUT |
| guest_user | guest-key-456 | GUEST | GET |

### Monitoring

```
http://localhost:8083/actuator/health
```

Prikazuje stanje Circuit Breaker-a (`smestajServiceCB`):
- `CLOSED` — servis radi normalno
- `OPEN` — servis je pao, zahtjevi se odbijaju odmah
- `HALF_OPEN` — testiranje da li je servis ponovo dostupan

---

## Horizontalno skaliranje i Load Balancing

### Tri instance booking-service-a

`docker-compose.yml` konfiguriše automatsko pokretanje 3 replike:

```yaml
booking-service:
  deploy:
    replicas: 3
```

Svaka instanca se automatski registruje u Eureki pod imenom `rezervacije-service` sa jedinstvenim `instance.id` (npr. `rezervacije-service:abc123`). API Gateway koristi `lb://booking-service` URI koji, oslanjajući se na Eureka registar, raspoređuje zahteve po **round-robin** principu.

### Stateless dizajn

Booking servis je projektovan bez serverskog stanja između zahteva:
- **Nema HTTP sesije** — svaki zahtev je nezavisan, autentifikacija ide kroz `X-API-KEY` header koji Gateway validira
- **Stanje je u bazi** — MySQL (`eco_tourism_db`) je jedini izvor istine; sve instance dele isti `db-mysql` kontejner
- **Asinhrono stanje** — promene statusa koje se propagiraju ka drugim servisima idu kroz RabbitMQ, ne kroz memoriju instance
- **Posledica:** bilo koja od 3 instance može obraditi bilo koji zahtev bez znanja o prethodnim zahtevima

---

## Observabilnost (Zadatak 3)

### Loki — centralizovani logovi

Svaka instanca šalje logove u Loki (`http://loki:3100/loki/api/v1/push`) sa metapodacima:

| Label | Vrednost |
|-------|---------|
| `app` | `booking-service` |
| `host` | `${HOSTNAME}` (jedinstveno po instanci) |
| `level` | `INFO` / `WARN` / `ERROR` |

Logovi se mogu pretraživati u Grafani po instanci koristeći `{app="booking-service", host="..."}`.

### Zipkin — distribuirano praćenje

Svaki zahtev koji prolazi kroz sistem dobija `traceId` na nivou API Gateway-a i prenosi se u W3C `traceparent` formatu (`00-{traceId}-{spanId}-01`). U Zipkin UI-u (`http://localhost:9411`) može se pratiti ceo put zahteva: Gateway → booking-service → accommodation-service.

Konfiguracija:
```properties
management.tracing.propagation.type=w3c
management.tracing.sampling.probability=1.0
management.zipkin.tracing.endpoint=http://zipkin:9411/api/v2/spans
```

### Grafana Dashboard

Dashboard "Zorana Dashboards" (`http://localhost:3000`) se osvežava svakih **10 sekundi** i sadrži:

**Loki vizualizacije (sistemske performanse):**
- **Log rate po instanci** — Time series grafikon koji prikazuje frekvenciju logova po `host` labelu, omogućavajući uočavanje neujednačene distribucije zahteva između instanci
- **Poslovni eventi po tipu** — Donut grafikon koji broji pojave ključnih poslovnih akcija (kreiranje polise, otkazivanje rezervacije, pretraga) iz logova u poslednjih 30 minuta

**MySQL vizualizacije (poslovna analitika):**
- **Raspodela statusa rezervacija** — Pie chart: `SELECT status, COUNT(*) FROM booking GROUP BY status`
- **Transakcije po načinu plaćanja** — Bar gauge: broj i ukupni iznos transakcija grupisanih po načinu plaćanja
- **Prihod po mesecima** — Bar chart: `SUM(total_price)` po mesecima za ne-otkazane rezervacije

---

## Dockerfile — višefazna izgradnja

```dockerfile
# Faza 1: Build (Maven + JDK)
FROM maven:3.9-eclipse-temurin-21-alpine AS builder
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn package -DskipTests

# Faza 2: Runtime (samo JRE)
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
```

Finalna slika sadrži samo JRE i izvršni `.jar` — bez Maven-a, izvornog koda ili privremenih build fajlova.

---

## Konfiguracija (iz Config Servera — `rezervacije-service.properties`)

Servis preuzima konfiguraciju iz eksternog Config Servera pri pokretanju. Ključni parametri:

```properties
# MySQL (Docker)
spring.datasource.url=jdbc:mysql://db-mysql:3306/eco_tourism_db
spring.datasource.username=root
spring.jpa.hibernate.ddl-auto=update

# RabbitMQ
spring.rabbitmq.host=rabbitmq
spring.rabbitmq.port=5672

# Circuit Breaker
resilience4j.circuitbreaker.instances.smestajServiceCB.failure-rate-threshold=50
resilience4j.circuitbreaker.instances.smestajServiceCB.wait-duration-in-open-state=10s

# Rate Limiter
resilience4j.ratelimiter.instances.basicLimit.limit-for-period=5
resilience4j.ratelimiter.instances.basicLimit.limit-refresh-period=10s

# Tracing
management.tracing.propagation.type=w3c
management.tracing.sampling.probability=1.0
management.zipkin.tracing.endpoint=http://zipkin:9411/api/v2/spans
```
