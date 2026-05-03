# Finansije Mikroservis

## Tehničke specifikacije

| | |
|---|---|
| **Framework** | Spring Boot 3.5.11 |
| **Jezik** | Java 21 |
| **Baza podataka** | H2 (in-memory) |
| **Port** | `8084` |
| **Dokumentacija** | Swagger UI kroz Gateway: `http://localhost:8080/swagger-ui.html` |
| **H2 konzola** | `http://localhost:8084/h2-console` |
| **Config Server** | `http://localhost:8888` (opciono) |

---

## Opis poslovne logike

Finansije mikroservis upravlja finansijskim aspektima eko-turističkog sistema. Servis omogućava:

- Kreiranje računa za rezervacije uz automatsko izračunavanje ukupnog iznosa
- Validaciju statusa rezervacije pri kreiranju računa (mora biti CONFIRMED ili COMPLETED)
- Sprečavanje kreiranja duplikata — ne može postojati aktivni račun za istu rezervaciju
- Evidentiranje transakcija plaćanja vezanih za račun
- Detekciju djelimično plaćenih računa (suma transakcija < totalAmount)
- Upravljanje načinima plaćanja i poreskim stopama
- Inter-servisnu komunikaciju sa Booking servisom putem Feign klijenta
- Zaštitu od pada zavisnih servisa putem Circuit Breaker mehanizma
- Zaštitu od preopterećenja putem Rate Limiter mehanizma

---

## Arhitekturni obrasci

### Strategy (Obračun poreza)

Implementiran u paketu `strategy` za kalkulaciju `totalAmount` pri kreiranju fakture. `InvoicesService` prima `TaxCalculationStrategy` putem constructor injection-a — Spring automatski injektira `@Primary` bean (`StandardTaxStrategy`) ako nije eksplicitno drugačije konfigurisano.

```
TaxCalculationStrategy (interfejs)
    ├── StandardTaxStrategy (@Primary) → subtotal × (1 + taxPercentage/100)
    ├── CompoundTaxStrategy            → subtotal × (1 + taxPercentage/100)²
    └── TaxExemptStrategy              → subtotal (bez promjene)
```

**Zašto je Strategy dobar fit:** Poreska logika za eko-turizam se mijenja po kategoriji smeštaja i sezonskim propisima. Strategy odvaja algoritam izračuna od `InvoicesService` — promjena poreske politike (npr. uvođenje složenog poreza) zahteva samo novu implementaciju interfejsa, bez ikakve izmene servisne klase.

---

### Factory (Procesori plaćanja)

Implementiran u paketu `factory` za procesiranje transakcija. `PaymentProcessorFactory.create(paymentMethod)` prima sve Spring beanove koji implementiraju `PaymentProcessor` kroz injektovanu listu, a zatim iz te liste bira procesor čija metoda `getSupportedMethod()` odgovara nazivu načina plaćanja na transakciji.

```
PaymentProcessor (interfejs)
    ├── BankTransferProcessor → procesira "BANK_TRANSFER" transakcije
    ├── CreditCardProcessor   → procesira "CREDIT_CARD" transakcije
    └── CashProcessor         → procesira "CASH" transakcije

PaymentProcessorFactory.create(paymentMethod) → vraća odgovarajući procesor
```

**Zašto je Factory dobar fit:** Sistem podržava više načina plaćanja, a svaki ima specifičnu logiku procesiranja. Factory centralizuje selekciju procesora i izoluje `TransactionsService` od te odluke — dodavanje novog načina plaćanja (npr. kriptovaluta) zahteva samo novu klasu koja implementira `PaymentProcessor`, bez izmene `TransactionsService` ili `PaymentProcessorFactory`.

---

## Inter-servisna komunikacija

### Feign klijenti

**BookingClient** — komunikacija sa Booking servisom (port 8083):

| Metoda | Endpoint | Opis                                                           |
|---|---|----------------------------------------------------------------|
| `getBookingById(id)` | GET /api/bookings/{id} | Dohvata rezervaciju po ID-u za validaciju pri kreiranju računa |
| `getAllBookings()` | GET /api/bookings | Dohvata sve rezervacije                                        |
| `calculateNightsForBooking(id)` | GET /api/bookings/{id}/nights | Izračunava broj noći za rezervaciju                            |

### Resilience mehanizmi

- **Circuit Breaker** (`bookingServiceCB`) — otvara se nakon 50% grešaka u prozoru od 10 poziva, čeka 10s, testira 3 poziva u polu-otvorenom stanju
- **Rate Limiter** (`basicLimit`) — maksimalno 5 zahteva na 10 sekundi, timeout 1000ms
- **Fallback metode** — svaki Feign poziv ima definisanu fallback metodu koja vraća HTTP 503 ili propagira 404

### Distribuirano praćenje (Trace ID)

`CorrelationIdFilter` automatski prima `X-Trace-Id` header i upisuje ga u MDC. `FeignClientInterceptor` propagira tri headera na svaki izlazni Feign poziv: `X-Trace-Id`, `X-Authenticated-User`, `X-User-Role`.

Format logova:
```
2026-04-14 10:15:30 [http-nio-8084-exec-1] INFO [traceId=abc123] ...
```

---

## Validacije pri kreiranju fakture

`InvoicesService.createInvoice()` izvršava sledeće poslovne provere pre upisa u bazu:

1. **Validacija statusa rezervacije** — rezervacija mora biti u statusu `CONFIRMED` ili `COMPLETED` (poziv Booking servisa)
2. **Validacija iznosa** — `totalPrice` rezervacije mora biti veći od 0
3. **Provjera duplikata** — ne smije postojati aktivan (ne-CANCELLED) račun za istu rezervaciju

---

## Implementirani endpointi

### Računi — `/api/invoices`

1. `GET` "/api/invoices"
   Dohvatanje svih računa
   Parametri:
   - status — String, nije obavezan, filter po statusu (UNPAID / PAID / CANCELLED / OVERDUE)
   - bookingId — Long, nije obavezan, filter po ID-u rezervacije

2. `GET` "/api/invoices/{id}"
   Dohvatanje računa po ID-u
   Parametri:
   - id — Long, obavezan, ID računa

3. `POST` "/api/invoices"
   Kreiranje novog računa — matematička kalkulacija + poziv Booking servisa
   Parametri: bookingId (@NotNull)
   Formula: totalAmount = subtotal × (1 + taxPercentage/100)
   Napomene: rezervacija mora biti CONFIRMED ili COMPLETED, ne smije postojati aktivan račun za istu rezervaciju
   Napomena: Rate Limiter zaštita — max 5 zahteva na 10 sekundi, Circuit Breaker zaštita — bookingServiceCB

4. `PUT` "/api/invoices/{id}"
   Izmjena statusa računa
   Napomena: nije dozvoljeno menjati račun u statusu PAID ili CANCELLED

5. `DELETE` "/api/invoices/{id}"
   Brisanje računa

6. `GET` "/api/invoices/bookings"
   Dohvatanje svih rezervacija iz Booking servisa putem Feign klijenta
   Napomena: Circuit Breaker zaštita — bookingServiceCB

7. `GET` "/api/invoices/bookings/{id}/nights"
   Dohvatanje broja noći za rezervaciju putem Feign klijenta
   Parametri:
   - id — Long, obavezan, ID rezervacije
   Napomena: Circuit Breaker zaštita — bookingServiceCB

8. `GET` "/api/invoices/partially-paid"
   Složeni upit — UNPAID račun gdje je suma transakcija manja od totalAmount
   (upit koji joinuje invoices i transactions i filtrira po uslovu Σ amount < totalAmount)

9. `GET` "/api/invoices/filter"
   Filter — Račune po nazivu poreske stope i minimalnom iznosu
   Parametri:
   - taxName — String, obavezan, naziv poreske stope (npr. "PDV 10%")
   - minAmount — Double, obavezan, minimalni ukupni iznos
   Sortiranje: opadajuće po totalAmount, isključeni CANCELLED računi

---

### Transakcije — `/api/transactions`

1. `GET` "/api/transactions"
   Dohvatanje svih transakcija
   Parametri:
   - invoiceId — Long, nije obavezan, filter po ID-u računa
   - paymentMethodId — Long, nije obavezan, filter po ID-u načina plaćanja

2. `GET` "/api/transactions/{id}"
   Dohvatanje transakcije po ID-u

3. `POST` "/api/transactions"
   Kreiranje nove transakcije — primjenjuje Factory obrazac
   Logika: `PaymentProcessorFactory.create(paymentMethod)` bira procesor na osnovu naziva načina plaćanja, potom poziva `processor.process(transaction)`

4. `DELETE` "/api/transactions/{id}"
   Brisanje transakcije

5. `GET` "/api/transactions/invoice/{invoiceId}/total"
   Matematička kalkulacija — suma svih transakcija za račun
   Formula: Σ amount svih transakcija gdje je invoiceId = traženi ID

---

### Načini plaćanja — `/api/payment-methods`

1. `GET` "/api/payment-methods"
   Dohvatanje svih načina plaćanja
   Parametri:
   - activeOnly — Boolean, nije obavezan, ako je true vraća samo aktivne

2. `GET` "/api/payment-methods/{id}"
   Dohvatanje načina plaćanja po ID-u

3. `POST` "/api/payment-methods"
   Kreiranje novog načina plaćanja
   Parametri: methodName (@NotBlank), isActive (@NotNull)

4. `PUT` "/api/payment-methods/{id}"
   Izmena načina plaćanja

5. `DELETE` "/api/payment-methods/{id}"
   Brisanje načina plaćanja

---

### Porezne stope — `/api/tax-rates`

1. `GET` "/api/tax-rates"
   Dohvatanje svih poreznih stopa
   Parametri:
   - minPercentage — Double, nije obavezan, donja granica opsega
   - maxPercentage — Double, nije obavezan, gornja granica opsega
   (oba parametra moraju biti prosleđena za filtriranje po opsegu)

2. `GET` "/api/tax-rates/{id}"
   Dohvatanje porezne stope po ID-u

3. `POST` "/api/tax-rates"
   Kreiranje nove porezne stope

4. `PUT` "/api/tax-rates/{id}"
   Izmjena porezne stope

5. `DELETE` "/api/tax-rates/{id}"
   Brisanje porezne stope

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
  "message": "Faktura sa ID-em 5 nije pronađena",
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
3. smestaj-service    (port 8081)
4. booking-service    (port 8083)  ← finansije-service zavisi od ovog
5. finansije-service  (port 8084)  ← ovaj servis
6. gateway-service    (port 8080)  ← poslednji obavezno
```

### Testiranje putem Swagger UI

Preporučeno — kroz Gateway (sa autentifikacijom):
```
http://localhost:8080/swagger-ui.html
```
Odaberi "Finansije Service" iz padajućeg menija i dodaj X-API-KEY u "Authorize".

Direktan pristup (bez Gateway autentifikacije):
```
http://localhost:8084/swagger-ui/index.html
```

### API ključevi za testiranje

| Korisnik | API ključ | Uloga | Prava |
|---|---|---|---|
| admin_user | admin-key-123 | ADMIN | GET, POST, PUT, DELETE |
| guide_user | guide-key-789 | GUIDE | GET, POST, PUT |
| guest_user | guest-key-456 | GUEST | GET |

### Monitoring Circuit Breaker-a

```
http://localhost:8084/actuator/health
```

Prikazuje stanje Circuit Breaker-a (`bookingServiceCB`):
- `CLOSED` — servis radi normalno
- `OPEN` — servis je pao, zahtjevi se odbijaju odmah
- `HALF_OPEN` — testiranje da li je servis ponovo dostupan

### Testiranje putem H2 konzole

```
URL:      http://localhost:8084/h2-console
JDBC URL: jdbc:h2:mem:financesdb
Username: sa
Password: (prazno)
```

**Inicijalni podaci (data.sql):**
- 3 porezne stope (20%, 10%, 0%)
- 4 načina plaćanja (Gotovina, Kreditna kartica, Bankovna transakcija, PayPal — neaktivan)
- 6 faktura u različitim statusima
- 3 transakcije

---

## Konfiguracija (application.properties)

```properties
spring.application.name=finansije-service
spring.config.import=optional:configserver:http://localhost:8888

server.port=8084

spring.datasource.url=jdbc:h2:mem:financesdb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=none
spring.sql.init.mode=always
spring.jpa.defer-datasource-initialization=true

# Circuit Breaker
resilience4j.circuitbreaker.instances.bookingServiceCB.register-health-indicator=true
resilience4j.circuitbreaker.instances.bookingServiceCB.sliding-window-size=10
resilience4j.circuitbreaker.instances.bookingServiceCB.failure-rate-threshold=50
resilience4j.circuitbreaker.instances.bookingServiceCB.wait-duration-in-open-state=10s
resilience4j.circuitbreaker.instances.bookingServiceCB.permitted-number-of-calls-in-half-open-state=3

# Rate Limiter
resilience4j.ratelimiter.instances.basicLimit.limit-for-period=5
resilience4j.ratelimiter.instances.basicLimit.limit-refresh-period=10s
resilience4j.ratelimiter.instances.basicLimit.timeout-duration=1000ms

# Actuator
management.endpoints.web.exposure.include=health,circuitbreakers,refresh
management.endpoint.health.show-details=always
management.health.circuitbreakers.enabled=true
```
