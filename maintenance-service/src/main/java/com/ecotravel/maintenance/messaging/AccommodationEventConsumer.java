package com.ecotravel.maintenance.messaging;

import com.ecotravel.maintenance.config.RabbitMQConfig;
import com.ecotravel.maintenance.model.MaintenanceLog;
import com.ecotravel.maintenance.model.WorkOrder;
import com.ecotravel.maintenance.repository.MaintenanceLogRepository;
import com.ecotravel.maintenance.repository.MaintenanceTaskRepository;
import com.ecotravel.maintenance.repository.WorkOrderRepository;
import com.ecotravel.maintenance.repository.WorkerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.sharedmodule.BookingCancelledEventDTO;
import org.example.sharedmodule.UnitCreatedEventDTO;
import org.example.sharedmodule.UnitDeletedEventDTO;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class AccommodationEventConsumer {

    private final WorkOrderRepository workOrderRepository;
    private final MaintenanceLogRepository maintenanceLogRepository;
    private final MaintenanceTaskRepository maintenanceTaskRepository;
    private final WorkerRepository workerRepository;

    // ============================================================
    // 1. UnitCreated — od accommodation-service
    // ============================================================

    /**
     * Kada se kreira nova smještajna jedinica, maintenance bilježi tu činjenicu
     * u MaintenanceLog kao historijski zapis (audit trail).
     */
    @RabbitListener(queues = RabbitMQConfig.MAINTENANCE_UNIT_CREATED_QUEUE)
    @Transactional
    public void handleUnitCreated(UnitCreatedEventDTO event) {
        log.info("[CONSUMER] Primljen UnitCreatedEvent: jedinica='{}' (ID={}), tip={}, grad={}",
                event.getUnitName(), event.getUnitId(), event.getUnitType(), event.getCity());

        try {
            // Kreiramo MaintenanceLog zapis kao audit trail nove jedinice
            MaintenanceLog auditLog = new MaintenanceLog();
            auditLog.setUnitId(event.getUnitId());
            auditLog.setTaskDescription("SISTEM: Nova smještajna jedinica registrovana u sistemu.");
            auditLog.setWorkerName("SYSTEM");
            auditLog.setCompletedAt(event.getOccurredAt());
            auditLog.setNotes(String.format(
                    "Jedinica '%s' (tip: %s, grad: %s, cijena: %.2f€/noć) registrovana asinhrono.",
                    event.getUnitName(), event.getUnitType(),
                    event.getCity(), event.getBasePricePerNight()
            ));

            maintenanceLogRepository.save(auditLog);
            log.info("[CONSUMER] Audit log kreiran za novu jedinicu ID={}", event.getUnitId());

        } catch (Exception e) {
            log.error("[CONSUMER] Greška pri obradi UnitCreatedEvent za jedinicu ID={}: {}",
                    event.getUnitId(), e.getMessage());
            throw e;
        }
    }

    // ============================================================
    // 2. UnitDeleted — od accommodation-service
    // ============================================================

    /**
     * Kada se briše smještajna jedinica, maintenance automatski otkazuje
     * sve PENDING radne naloge za tu jedinicu.
     *
     * Otpornost: Ako je booking-service bio u kvaru i poruka čekala u queue-u,
     * ovdje ćemo je obraditi čim se maintenance restartuje.
     */
    @RabbitListener(queues = RabbitMQConfig.MAINTENANCE_UNIT_DELETED_QUEUE)
    @Transactional
    public void handleUnitDeleted(UnitDeletedEventDTO event) {
        log.info("[CONSUMER] Primljen UnitDeletedEvent: jedinica='{}' (ID={})",
                event.getUnitName(), event.getUnitId());

        try {
            // Pronađi sve PENDING naloge za obrisanu jedinicu
            List<WorkOrder> pendingOrders = workOrderRepository
                    .findByUnitIdAndStatus(event.getUnitId(), "PENDING");

            if (pendingOrders.isEmpty()) {
                log.info("[CONSUMER] Nema PENDING naloga za jedinicu ID={}", event.getUnitId());
                return;
            }

            // Otkaži sve pronađene naloge
            for (WorkOrder order : pendingOrders) {
                order.setStatus("CANCELLED");
                order.setCompletedAt(LocalDateTime.now());
                workOrderRepository.save(order);

                log.info("[CONSUMER] Radni nalog ID={} otkazan jer je jedinica ID={} obrisana.",
                        order.getId(), event.getUnitId());
            }

            // Zabilježi u MaintenanceLog
            MaintenanceLog cancelLog = new MaintenanceLog();
            cancelLog.setUnitId(event.getUnitId());
            cancelLog.setTaskDescription("SISTEM: Radni nalozi automatski otkazani — jedinica obrisana.");
            cancelLog.setWorkerName("SYSTEM");
            cancelLog.setCompletedAt(event.getOccurredAt());
            cancelLog.setNotes(String.format(
                    "Jedinica '%s' (ID=%d) obrisana. Automatski otkazano %d PENDING naloga.",
                    event.getUnitName(), event.getUnitId(), pendingOrders.size()
            ));
            maintenanceLogRepository.save(cancelLog);

        } catch (Exception e) {
            log.error("[CONSUMER] Greška pri obradi UnitDeletedEvent za jedinicu ID={}: {}",
                    event.getUnitId(), e.getMessage());
            throw e;
        }
    }

    // ============================================================
    // 3. BookingCancelled — od booking-service (TUĐI SERVIS)
    // ============================================================

    /**
     * Kada se otkaže rezervacija, maintenance automatski kreira radni nalog
     * za čišćenje smještajne jedinice.
     *
     * Ovo ispunjava uslov: "barem 1 metoda komunicira sa mikroservisom
     * za koji je zadužen drugi član tima."
     *
     * Logika:
     * - Pronalazimo prvi slobodan CLEANER radnik
     * - Pronalazimo "Generalno čišćenje" task
     * - Kreiramo WorkOrder sa scheduledFor = startDate rezervacije (kad gost treba otići)
     */
    @RabbitListener(queues = RabbitMQConfig.MAINTENANCE_BOOKING_CANCELLED_QUEUE)
    @Transactional
    public void handleBookingCancelled(BookingCancelledEventDTO event) {
        log.info("[CONSUMER] Primljen BookingCancelledEvent: booking={}, period={} → {}",
                event.getBookingId(), event.getStartDate(), event.getEndDate());

        try {
            // Pronađi "Generalno čišćenje" task (ili prvi dostupan)
            var cleaningTask = maintenanceTaskRepository
                    .findByTaskNameContainingIgnoreCase("čišćenje")
                    .stream()
                    .findFirst()
                    .orElseGet(() -> maintenanceTaskRepository.findAll()
                            .stream()
                            .findFirst()
                            .orElse(null));

            if (cleaningTask == null) {
                log.warn("[CONSUMER] Nema dostupnog taska za čišćenje. Nalog nije kreiran za booking={}",
                        event.getBookingId());
                return;
            }

            // Pronađi prvog slobodnog CLEANER-a (bez previše PENDING naloga)
            var availableWorker = workerRepository.findBySpecialty("CLEANER")
                    .stream()
                    .filter(w -> workOrderRepository.findByWorker_Id(w.getId())
                            .stream()
                            .filter(o -> "PENDING".equals(o.getStatus()) || "IN_PROGRESS".equals(o.getStatus()))
                            .count() < 3)
                    .findFirst()
                    .orElse(null);

            if (availableWorker == null) {
                log.warn("[CONSUMER] Nema slobodnog CLEANER radnika za booking={}. " +
                        "Nalog će biti kreiran bez dodjele.", event.getBookingId());
                // Možemo kreirati nalog bez workera (null) ili logujemo i preskačemo
                // Za demonstraciju — logujemo i ne kreiramo nalog:
                MaintenanceLog errorLog = new MaintenanceLog();
                // unitId nije dostupan u BookingCancelledEventDTO — koristimo bookingId kao referencu
                errorLog.setUnitId(null);
                errorLog.setTaskDescription("SISTEM: Nije kreiran nalog za čišćenje — nema slobodnog CLEANER-a.");
                errorLog.setWorkerName("SYSTEM");
                errorLog.setCompletedAt(LocalDateTime.now());
                errorLog.setNotes("BookingID=" + event.getBookingId());
                maintenanceLogRepository.save(errorLog);
                return;
            }

            // Kreira radni nalog za čišćenje
            // Napomena: BookingCancelledEventDTO nema unitId — koristimo bookingId kao privremenu referencu.
            // Ako koleginica doda unitId u event, ažuriraj ovaj kod.
            WorkOrder cleaningOrder = new WorkOrder();
            cleaningOrder.setUnitId(event.getBookingId()); // placeholder dok shared DTO ne dobije unitId
            cleaningOrder.setWorker(availableWorker);
            cleaningOrder.setTask(cleaningTask);
            cleaningOrder.setStatus("PENDING");
            // Zakaži na datum početka rezervacije (kad gost odlazi)
            LocalDateTime scheduledTime = event.getStartDate().toInstant()
                    .atZone(java.time.ZoneId.systemDefault())
                    .toLocalDateTime()
                    .withHour(11).withMinute(0); // checkout je obično u 11h
            cleaningOrder.setScheduledFor(scheduledTime);

            WorkOrder saved = workOrderRepository.save(cleaningOrder);

            log.info("[CONSUMER] Nalog za čišćenje ID={} kreiran automatski za booking={}, " +
                            "radnik='{}', zakazan za {}",
                    saved.getId(), event.getBookingId(),
                    availableWorker.getFirstName() + " " + availableWorker.getLastName(),
                    scheduledTime);

        } catch (Exception e) {
            log.error("[CONSUMER] Greška pri obradi BookingCancelledEvent za booking={}: {}",
                    event.getBookingId(), e.getMessage());
            throw e;
        }
    }
}