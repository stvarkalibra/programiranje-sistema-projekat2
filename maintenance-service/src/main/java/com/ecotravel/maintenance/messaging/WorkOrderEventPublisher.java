package com.ecotravel.maintenance.messaging;

import com.ecotravel.maintenance.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.sharedmodule.WorkOrderCompletedEventDTO;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class WorkOrderEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    /**
     * Publishuje događaj kada radni nalog pređe u status COMPLETED.
     * Accommodation servis sluša i automatski briše MAINTENANCE blokadu iz kalendara,
     * čime se jedinica oslobađa za rezervacije.
     */
    public void publishWorkOrderCompleted(Long workOrderId, Long unitId,
                                          String taskName, String workerFullName,
                                          LocalDateTime completedAt, LocalDateTime scheduledFor) {
        WorkOrderCompletedEventDTO event = new WorkOrderCompletedEventDTO(
                workOrderId,
                unitId,
                taskName,
                workerFullName,
                completedAt,
                scheduledFor
        );

        try {
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.MAINTENANCE_EXCHANGE,
                    RabbitMQConfig.WORK_ORDER_COMPLETED_ROUTING_KEY,
                    event
            );
            log.info("[PUBLISHER] WorkOrderCompletedEvent poslan: nalog={}, jedinica={}, radnik='{}'",
                    workOrderId, unitId, workerFullName);
        } catch (Exception e) {
            // Poruka ostaje u brokeru ako RabbitMQ privremeno nije dostupan.
            // Ovdje samo logujemo jer je operacija već uspješno upisana u bazu.
            log.error("[PUBLISHER] Greška pri slanju WorkOrderCompletedEvent za nalog={}: {}",
                    workOrderId, e.getMessage());
        }
    }
}