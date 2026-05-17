package com.ecotravel.accommodation.messaging;

import com.ecotravel.accommodation.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.sharedmodule.UnitCreatedEventDTO;
import org.example.sharedmodule.UnitDeletedEventDTO;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class UnitEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    /**
     * Publishuje događaj kada se kreira nova smještajna jedinica.
     * Maintenance servis sluša ovaj događaj i loguje novu jedinicu.
     */
    public void publishUnitCreated(Long unitId, String unitName, String unitType,
                                   String city, Double basePricePerNight) {
        UnitCreatedEventDTO event = new UnitCreatedEventDTO(
                unitId,
                unitName,
                unitType,
                city,
                basePricePerNight,
                LocalDateTime.now()
        );

        try {
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.ACCOMMODATION_EXCHANGE,
                    RabbitMQConfig.UNIT_CREATED_ROUTING_KEY,
                    event
            );
            log.info("[PUBLISHER] UnitCreatedEvent poslan za jedinicu ID={}, naziv='{}'",
                    unitId, unitName);
        } catch (Exception e) {
            // Poruka nije izgubljena ako je RabbitMQ nedostupan — Spring AMQP će je
            // pokušati ponovo. Logujemo grešku ali ne prekidamo tok kreiranja.
            log.error("[PUBLISHER] Greška pri slanju UnitCreatedEvent za ID={}: {}",
                    unitId, e.getMessage());
        }
    }

    /**
     * Publishuje događaj kada se briše smještajna jedinica.
     * Maintenance servis otkazuje sve PENDING naloge za tu jedinicu.
     */
    public void publishUnitDeleted(Long unitId, String unitName) {
        UnitDeletedEventDTO event = new UnitDeletedEventDTO(
                unitId,
                unitName,
                LocalDateTime.now()
        );

        try {
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.ACCOMMODATION_EXCHANGE,
                    RabbitMQConfig.UNIT_DELETED_ROUTING_KEY,
                    event
            );
            log.info("[PUBLISHER] UnitDeletedEvent poslan za jedinicu ID={}, naziv='{}'",
                    unitId, unitName);
        } catch (Exception e) {
            log.error("[PUBLISHER] Greška pri slanju UnitDeletedEvent za ID={}: {}",
                    unitId, e.getMessage());
        }
    }
}