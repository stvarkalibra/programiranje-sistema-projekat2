#!/bin/bash

# ============================================================
#  ECO TOURISM – Priprema, pokretanje i verifikacija sistema
# ============================================================

set -e

echo "============================================"
echo "  ECO TOURISM - Pokretanje sistema"
echo "============================================"

echo ""
echo "=== 1. Gašenje i čišćenje prethodnog stanja ==="
docker compose down --remove-orphans
docker image prune -f

echo ""
echo "=== 2. Pokretanje sistema ==="
echo "(Maven build se izvršava unutar Dockera – multi-stage build)"
echo "(booking-service se pokreće sa 3 instance prema deploy.replicas)"
docker compose up --build -d

echo ""
echo "Čekam 35 sekundi da se svi servisi, Eureka i healthchecks usaglase..."
sleep 35

echo ""
echo "=== 3. Čišćenje zaostalih poruka iz RabbitMQ redova ==="

# ── Booking / Finances queue-ovi ──────────────────────────
docker exec rabbitmq rabbitmqctl purge_queue transaction_queue               || echo "  -> transaction_queue još ne postoji, preskačam"
docker exec rabbitmq rabbitmqctl purge_queue availability_queue              || echo "  -> availability_queue još ne postoji, preskačam"
docker exec rabbitmq rabbitmqctl purge_queue booking_feedback_queue          || echo "  -> booking_feedback_queue još ne postoji, preskačam"
docker exec rabbitmq rabbitmqctl purge_queue booking_time_slot_deleted_queue || echo "  -> booking_time_slot_deleted_queue još ne postoji, preskačam"
docker exec rabbitmq rabbitmqctl purge_queue transaction_feedback_queue      || echo "  -> transaction_feedback_queue još ne postoji, preskačam"

# ── Accommodation queue-ovi (Anja) ────────────────────────
docker exec rabbitmq rabbitmqctl purge_queue accommodation.workorder.completed.queue || echo "  -> accommodation.workorder.completed.queue još ne postoji, preskačam"

# ── Maintenance queue-ovi (Anja) ──────────────────────────
docker exec rabbitmq rabbitmqctl purge_queue maintenance.unit.created.queue          || echo "  -> maintenance.unit.created.queue još ne postoji, preskačam"
docker exec rabbitmq rabbitmqctl purge_queue maintenance.unit.deleted.queue          || echo "  -> maintenance.unit.deleted.queue još ne postoji, preskačam"
docker exec rabbitmq rabbitmqctl purge_queue maintenance.booking.cancelled.queue     || echo "  -> maintenance.booking.cancelled.queue još ne postoji, preskačam"

echo ""
echo "=== 4. Verifikacija statusa svih servisa ==="
docker compose ps

echo ""
echo "=== Broj podignutih instanci booking-service ==="
docker compose ps booking-service

echo ""
echo "=== Provjera zdravlja ključnih servisa ==="
check_health() {
  local name=$1
  local url=$2
  local status
  status=$(curl -s -o /dev/null -w "%{http_code}" --max-time 5 "$url" 2>/dev/null || echo "ERR")
  if [ "$status" = "200" ]; then
    echo "  ✓ $name → UP ($url)"
  else
    echo "  ✗ $name → NE ODGOVARA ($url) [HTTP $status]"
  fi
}

check_health "Config Service"         "http://localhost:8888/actuator/health"
check_health "Gateway"                "http://localhost:8080/actuator/health"
check_health "Discovery (Eureka)"     "http://localhost:8761/actuator/health"
check_health "Accommodation Service"  "http://localhost:8080/api/accommodation/actuator/health"
check_health "Maintenance Service"    "http://localhost:8080/api/maintenance/actuator/health"
check_health "Zipkin"                 "http://localhost:9411/actuator/health"
check_health "Loki"                   "http://localhost:3100/ready"
check_health "Grafana"                "http://localhost:3000/api/health"

echo ""
echo "============================================"
echo "  SISTEM SPREMAN"
echo "============================================"
echo "  Eureka Dashboard : http://localhost:8761"
echo "  API Gateway      : http://localhost:8080"
echo "  Zipkin Tracing   : http://localhost:9411"
echo "  Grafana          : http://localhost:3000  (admin/admin)"
echo "  RabbitMQ UI      : http://localhost:15672 (guest/guest)"
echo "  Adminer (DB)     : http://localhost:8085"
echo "============================================"