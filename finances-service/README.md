# Finansije Mikroservis

## Tehničke specifikacije

| | |
|---|---|
| **Framework** | Spring Boot 3.5.11 |
| **Jezik** | Java 21 |
| **Baza podataka** | MySQL 8.0 (Docker kontejner `db-mysql`, baza `eco_tourism_db`) |
| **Port** | `8084` |
| **Servisni registar** | Eureka (`http://discovery-service:8761`) |
| **Dokumentacija** | Swagger UI kroz Gateway: `http://localhost:8080/swagger-ui.html` |
| **Config Server** | `http://localhost:8888` |
| **Logovi** | Konzola + Loki (`http://loki:3100`) |
| **Tracing** | Zipkin (`http://zipkin:9411`), W3C `traceparent` format |

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

API Gateway generiše `traceparent` header u W3C formatu (`00-{traceId}-{spanId}-01`) i propagira ga kroz celu mrežu. Svaki mikroservis nasljeđuje `traceId` i upisuje ga u sve logove putem MDC-a.

Format konzolnih logova:
```
2026-04-14 10:15:30 [http-nio-8084-exec-1] INFO  [finansije-service] [traceId=abc123def456] o.e.r.s.InvoicesService - Faktura kreirana
```

---

## Asinhrona komunikacija (RabbitMQ)

Finansije servis učestvuje u Event-Driven arhitekturi kao primarni potrošač finansijskih događaja koje generiše Booking servis. Servis ostaje potpuno operativan čak i u slučaju privremene nedostupnosti Booking servisa.

### Konfiguracija redova i razmenjivača

| Red | Razmenjivač | Routing ključ | Smer | Uloga |
|-----|------------|---------------|------|-------|
| `transaction_queue` | `transaction_exchange` | `transaction_routing_key` | Prima ← Booking | Finansijski događaji pri promeni statusa rezervacije |
| `transaction_feedback_queue` | `transaction_feedback_exchange` | `feedback_key` | Šalje → Booking | Povratna informacija o uspešnoj obradi transakcije |

### Događaji koje servis prima i obrađuje

| Izvor | Red | Akcija u Finansije servisu |
|-------|-----|---------------------------|
| booking-service (status → CONFIRMED) | `transaction_queue` | Automatsko kreiranje transakcije i ažuriranje statusa fakture |

### Otpornost na greške

- **Trajan red** (`durable=true`) — poruke preživljavaju restart RabbitMQ brokera i ne gube se
- Finansije servis obrađuje poruke nezavisno od dostupnosti Booking servisa — kada poruka stigne, transakcija se kreira bez HTTP poziva
- **Demonstracija otpornosti:** terminacijom `finances-service` kontejnera poruke se akumuliraju u `transaction_queue`; pri ponovnom pokretanju servis automatski procesuira sve čekajuće poruke i sinhronizuje stanje

### Tok poruke (primer: uplata po potvrdi rezervacije)

```
booking-service: PATCH /api/bookings/{id}/status?status=CONFIRMED
        ↓
RabbitTemplate.convertAndSend("transaction_exchange", "transaction_routing_key", event)
        ↓
[RabbitMQ broker — transaction_queue]
        ↓
finances-service: TransactionEventListener.onTransactionEvent()
        ↓
InvoicesService.createInvoice() + TransactionsService.createTransaction()
        ↓
RabbitTemplate.convertAndSend("transaction_feedback_exchange", feedback)
        ↓
booking-service: BookingFeedbackListener (potvrda uspešne obrade)
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
| Swagger UI | `http://localhost:8080/swagger-ui.html` | odaberi "Finansije Service" |
| Eureka Dashboard | `http://localhost:8761` | prikaz instanci servisa |
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
http://localhost:8084/actuator/health
```

Prikazuje stanje Circuit Breaker-a (`bookingServiceCB`):
- `CLOSED` — servis radi normalno
- `OPEN` — servis je pao, zahtjevi se odbijaju odmah
- `HALF_OPEN` — testiranje da li je servis ponovo dostupan

---

## Stateless dizajn

Finansije servis je projektovan bez serverskog stanja između zahteva:
- **Nema HTTP sesije** — svaki zahtev je nezavisan, autentifikacija ide kroz `X-API-KEY` header koji Gateway validira
- **Stanje je u bazi** — MySQL (`eco_tourism_db`) je jedini izvor istine za fakture, transakcije i načine plaćanja
- **Asinhrono primanje** — finansijski događaji od Booking servisa stižu kroz RabbitMQ, ne kroz direktne HTTP pozive; servis obrađuje poruke iz reda, ne pamti stanje između njih
- **Posledica:** servis može biti horizontalno skaliran bez ikakvih izmena u kodu

---

## Observabilnost (Zadatak 3)

### Loki — centralizovani logovi

Servis šalje logove u Loki (`http://loki:3100/loki/api/v1/push`) sa metapodacima:

| Label | Vrednost |
|-------|---------|
| `app` | `finances-service` |
| `host` | `${HOSTNAME}` (jedinstveno po instanci) |
| `level` | `INFO` / `WARN` / `ERROR` |

Logovi se mogu pretraživati u Grafani korišćenjem `{app="finances-service"}`.

### Zipkin — distribuirano praćenje

Svaki zahtev koji prolazi kroz sistem dobija `traceId` na nivou API Gateway-a i prenosi se u W3C `traceparent` formatu. U Zipkin UI-u može se pratiti ceo put zahteva: Gateway → finances-service → booking-service.

Konfiguracija:
```properties
management.tracing.propagation.type=w3c
management.tracing.sampling.probability=1.0
management.zipkin.tracing.endpoint=http://zipkin:9411/api/v2/spans
```

### Grafana Dashboard

Dashboard "Zorana Dashboards" (`http://localhost:3000`) se osvežava svakih **10 sekundi** i sadrži vizualizacije iz oba mikroservisa:

**Loki vizualizacije (sistemske performanse):**
- **Log rate po instanci** — prati frekvenciju logova booking-service instanci
- **Poslovni eventi po tipu** — broji kreiranje polise, otkazivanje rezervacija i pretrage u realnom vremenu

**MySQL vizualizacije (poslovna analitika):**
- **Raspodela statusa rezervacija** — Pie chart: distribucija statusa u `booking` tabeli
- **Transakcije po načinu plaćanja** — Bar gauge: broj i ukupni iznos po načinu plaćanja iz `transaction` + `payment_method` tabela
- **Prihod po mesecima** — Bar chart: mesečni prihod iz `booking.total_price` za ne-otkazane rezervacije

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

## Konfiguracija (iz Config Servera — `finansije-service.properties`)

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
resilience4j.circuitbreaker.instances.bookingServiceCB.failure-rate-threshold=50
resilience4j.circuitbreaker.instances.bookingServiceCB.wait-duration-in-open-state=10s

# Rate Limiter
resilience4j.ratelimiter.instances.basicLimit.limit-for-period=5
resilience4j.ratelimiter.instances.basicLimit.limit-refresh-period=10s
resilience4j.ratelimiter.instances.basicLimit.timeout-duration=1000ms

# Tracing
management.tracing.propagation.type=w3c
management.tracing.sampling.probability=1.0
management.zipkin.tracing.endpoint=http://zipkin:9411/api/v2/spans
```
