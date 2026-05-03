# Sistem za upravljanje eko-turizmom

Spring Cloud mikroservisna aplikacija za upravljanje eko-turističkim smestajem — obuhvata smeštajne jedinice, rezervacije, održavanje i finansije.

---

## Arhitektura

```
                        ┌─────────────────────┐
                        │   API Gateway :8080  │
                        │  (Spring Cloud GW)   │
                        └──────────┬──────────┘
                                   │ rutira /api/{service}/**
           ┌───────────┬───────────┼───────────┬──────────────┐
           ▼           ▼           ▼           ▼              ▼
     smestaj      odrzavanje    booking    finances         auth
      :8081          :8082       :8083       :8084          :8085

                        ┌─────────────────────┐
                        │  Config Server :8888 │
                        │ (Spring Cloud Config)│
                        └─────────────────────┘
                          ▲ preuzimaju svi servisi pri pokretanju
```

---

## Servisi

| Servis | Port | Odgovornost |
|---|---|---|
| `config-service` | 8888 | Centralizovana konfiguracija (podržana Git-om) |
| `auth-service` | 8085 | Autentifikacija i validacija API ključeva |
| `smestaj-service` | 8081 | Smeštajne jedinice, dostupnost, cene |
| `booking-service` | 8083 | Životni ciklus rezervacija, politike otkazivanja |
| `odrzavanje-service` | 8082 | Radni nalozi za održavanje i radnici |
| `finances-service` | 8084 | Fakturisanje, plaćanja, obračun poreza |
| `gateway-service` | 8080 | Rutiranje, CORS, agregacija Swagger dokumentacije |

Svaki servis ima sopstveni `README.md` sa dokumentacijom API endpointa.

---

## Komunikacija između servisa

Servisi komuniciraju sinhrono putem REST-a koristeći **Spring Cloud OpenFeign**. Svi pozivi između servisa zaštićeni su pomoću **Resilience4j** (Circuit Breaker + Retry + Rate Limiter).

```
booking-service    ──Feign──▶  smestaj-service   (validacija jedinice i cena)
odrzavanje-service ──Feign──▶  smestaj-service   (provera postojanja jedinice)
odrzavanje-service ──Feign──▶  booking-service   (provera statusa rezervacije)
finances-service   ──Feign──▶  booking-service   (status rezervacije pre fakturisanja)
```

**Resilience šabloni primenjeni na sve Feign pozive:**

- **Circuit Breaker** — otvara se nakon 50% grešaka u kliznom prozoru; polu-otvoren nakon 10 s
- **Retry** — do 3 pokušaja sa pauzom od 2 s (odrzavanje-service)
- **Rate Limiter** — 5 zahteva / 10 s po endpointu

Zaglavlje `X-Trace-Id` prosleđuje se kroz sve pozive servisa radi distribuiranog praćenja.

---

## Pokretanje sistema

Ne postoji Docker Compose podešavanje — svaki servis se pokreće ručno. **Redosled pokretanja je važan** zbog zavisnosti između servisa.

### Preduslov — konfiguracija pristupa Git repozitorijumu

`config-service` preuzima konfiguraciju iz privatnog GitHub repozitorijuma. U fajlu `config-service/src/main/resources/application.properties` su podešeni GitHub korisničko ime i Personal Access Token (PAT).



```
# 1. Config Server — mora se pokrenuti prvi (svi ostali servisi preuzimaju konfiguraciju od njega)

# 2. Auth servis

# 3. Smestaj servis — mora biti pokrenut pre booking i odrzavanje servisa

# 4. Booking servis

# 5. Odrzavanje servis

# 6. Finances servis

# 7. API Gateway — mora se pokrenuti poslednji
```

---

## Razvojni alati

**Swagger UI** :
```
http://localhost:8080/swagger-ui.html
```

**H2 konzola u memoriji** (dostupna za smestaj, booking, odrzavanje, finances):
```
http://localhost:{port}/h2-console
JDBC URL: jdbc:h2:mem:{dbname}    (npr. smestajdb, orderdb, odrzavanjedb, financesdb)
Korisnik: sa    Lozinka: (prazno)
```

**Test API ključevi:**
| Uloga | Ključ |
|---|---|
| Admin | `admin-key-123` |
| Vodič | `guide-key-789` |
| Gost | `guest-key-456` |

**Actuator zdravlje i stanje circuit breaker-a:**
```
http://localhost:{port}/actuator/health
http://localhost:{port}/actuator/health/circuitbreakers
```

---

## Tehnološki stack

- **Java 17–21**, Spring Boot 3.5.x, Maven
- **Spring Cloud 2025.0.x** — Config Server, Gateway, OpenFeign
- **Resilience4j** — Circuit Breaker, Retry, Rate Limiter
- **H2** (u memoriji) / **SQLite** (auth-service)
- **MapStruct**, **Lombok**, **Hibernate Validator**
- **SpringDoc OpenAPI** — Swagger UI po servisu
