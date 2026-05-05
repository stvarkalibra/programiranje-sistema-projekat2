# Maintenance Service

Ovo je mikroservis za upravljanje održavanjem smještajnih objekata u okviru Eco-Travel & Adventure sistema.

## Tehnologije

- Spring Boot 3.4.1
- Java 21
- Port: 8081
- Baza: H2 (in-memory)
- Schema: `maintenancedb`

## Šta servis radi?

Servis pokriva sve vezano za održavanje:

- **Radnici (Workers)**  
  Evidencija osoblja sa različitim specijalnostima (električar, vodoinstalater, higijeničar...).

- **Tipovi zadataka (Tasks)**  
  Definicija vrsta poslova sa procijenjenim trajanjem.

- **Radni nalozi (Work orders)**  
  Povezuju radnika, zadatak i smještajnu jedinicu.  
  Status ide samo unaprijed: `PENDING → IN_PROGRESS → COMPLETED`.

- **Historija održavanja (Maintenance logs)**  
  Čuva sve završene radove po jedinici.

- **Opterećenje radnika**  
  Može se dobiti pregled koliko svaki radnik ima naloga i koliko sati radi.

## Endpointi

### Workers (/api/workers)

- `GET /api/workers` — svi radnici
- `GET /api/workers/{id}` — jedan radnik
- `GET /api/workers/filter/specialty` — filter po specijalnosti
- `GET /api/workers/{id}/summary` — pregled opterećenja
- `POST /api/workers` — dodavanje radnika
- `PUT /api/workers/{id}` — izmjena
- `DELETE /api/workers/{id}` — brisanje

### Tasks (`/api/tasks`)

- `GET /api/tasks`
- `GET /api/tasks/{id}`
- `GET /api/tasks/filter/max-duration`
- `POST /api/tasks`
- `PUT /api/tasks/{id}`
- `DELETE /api/tasks/{id}`

### Work Orders (`/api/work-orders`)

- `GET /api/work-orders`
- `GET /api/work-orders/{id}`
- `GET /api/work-orders/filter/unit/{unitId}`
- `GET /api/work-orders/filter/status`
- `POST /api/work-orders` — kreira nalog (PENDING)
- `PATCH /api/work-orders/{id}/status` — promjena statusa
- `DELETE /api/work-orders/{id}`

### Maintenance Logs (`/api/maintenance-logs`)

- `GET /api/maintenance-logs`
- `GET /api/maintenance-logs/{id}`
- `GET /api/maintenance-logs/unit/{unitId}`
- `POST /api/maintenance-logs`
- `DELETE /api/maintenance-logs/{id}`

## Opterećenje radnika

Endpoint:  
`GET /api/workers/{id}/summary`

Vraća:
- broj naloga po statusu
- ukupne sate za aktivne naloge (`PENDING + IN_PROGRESS`)