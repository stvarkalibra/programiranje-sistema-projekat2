#!/bin/bash

set -e

echo "============================================"
echo "  ECO TOURISM - Pokretanje sistema"
echo "============================================"

echo ""
echo "=== 1. Gasenje i ciscenje prethodnog stanja ==="
docker compose down --remove-orphans
docker image prune -f

echo ""
echo "=== 2. Pokretanje sistema ==="
echo "(Maven build se izvrsava unutar Dockera - multi-stage build)"
echo "(booking-service se pokrece sa 3 instance prema deploy.replicas u docker-compose.yml)"
docker compose up --build -d

echo ""
echo "Cekam 35 sekundi da se svi servisi, Eureka i healthchecks usaglase..."
sleep 35

echo ""
echo "=== 3. Ciscenje zaostalih poruka iz RabbitMQ redova ==="
docker exec rabbitmq rabbitmqctl purge_queue transaction_queue        || echo "  -> transaction_queue jos ne postoji, preskacam"
docker exec rabbitmq rabbitmqctl purge_queue availability_queue       || echo "  -> availability_queue jos ne postoji, preskacam"
docker exec rabbitmq rabbitmqctl purge_queue booking_feedback_queue   || echo "  -> booking_feedback_queue jos ne postoji, preskacam"
docker exec rabbitmq rabbitmqctl purge_queue booking_time_slot_deleted_queue || echo "  -> booking_time_slot_deleted_queue jos ne postoji, preskacam"
docker exec rabbitmq rabbitmqctl purge_queue transaction_feedback_queue || echo "  -> transaction_feedback_queue jos ne postoji, preskacam"

echo ""
echo "=== 4. Verifikacija statusa svih servisa ==="
docker compose ps

echo ""
echo "=== Broj podignutih instanci booking-service ==="
docker compose ps booking-service

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
