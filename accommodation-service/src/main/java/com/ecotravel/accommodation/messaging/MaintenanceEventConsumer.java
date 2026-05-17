package com.ecotravel.accommodation.messaging;

import com.ecotravel.accommodation.config.RabbitMQConfig;
import com.ecotravel.accommodation.repository.AvailabilityCalendarRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.sharedmodule.WorkOrderCompletedEventDTO;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
@Slf4j
public class MaintenanceEventConsumer {

    private final AvailabilityCalendarRepository availabilityCalendarRepository;

    /**
     * Sluša na WorkOrderCompleted eventi od maintenance servisa.
     *
     * Logika: Kada maintenance završi radni nalog (status → COMPLETED),
     * accommodation automatski pronalazi i briše MAINTENANCE blokadu
     * u AvailabilityCalendar za tu jedinicu na taj datum.
     *
     * Ovo oslobađa jedinicu za buduće rezervacije.
     *
     * containerFactory="rabbitListenerContainerFactory" koristi
     * Jackson2JsonMessageConverter definisan u RabbitMQConfig.
     */
    @RabbitListener(queues = RabbitMQConfig.ACCOMMODATION_WORK_ORDER_COMPLETED_QUEUE)
    @Transactional
    public void handleWorkOrderCompleted(WorkOrderCompletedEventDTO event) {
        log.info("[CONSUMER] Primljen WorkOrderCompletedEvent: nalog={}, jedinica={}, završen={}",
                event.getWorkOrderId(), event.getUnitId(), event.getCompletedAt());

        try {
            LocalDate maintenanceDate = event.getScheduledFor().toLocalDate();

            // Pronađi i obriši MAINTENANCE blokadu za tu jedinicu na taj datum
            int deleted = availabilityCalendarRepository
                    .deleteByUnitIdAndReasonAndDateRange(
                            event.getUnitId(),
                            "MAINTENANCE",
                            maintenanceDate
                    );

            if (deleted > 0) {
                log.info("[CONSUMER] Oslobođeno {} MAINTENANCE blokada za jedinicu ID={} na datum {}",
                        deleted, event.getUnitId(), maintenanceDate);
            } else {
                log.warn("[CONSUMER] Nije pronađena MAINTENANCE blokada za jedinicu ID={} na datum {}. " +
                        "Možda nije bila kreirana ili je već obrisana.", event.getUnitId(), maintenanceDate);
            }

        } catch (Exception e) {
            // Ako obrada ne uspije, poruka se vraća u queue (acknowledgment nije poslan)
            log.error("[CONSUMER] Greška pri obradi WorkOrderCompletedEvent za nalog={}: {}",
                    event.getWorkOrderId(), e.getMessage());
            throw e; // re-throw da Spring AMQP zna da nack-uje poruku
        }
    }
}