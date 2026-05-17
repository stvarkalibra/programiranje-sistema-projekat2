package org.raflab.avanturaservice.messaging;

import lombok.RequiredArgsConstructor;
import org.example.sharedmodule.TimeSlotDeletedEventDTO;
import org.raflab.avanturaservice.config.RabbitMQConfig;
import org.raflab.avanturaservice.entityLayer.TimeSlot;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TimeSlotDeletedEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public void publishTimeSlotDeleted(TimeSlot timeSlot) {
        TimeSlotDeletedEventDTO event = new TimeSlotDeletedEventDTO(
                timeSlot.getId(),
                timeSlot.getAdventure().getId(),
                timeSlot.getStartTime(),
                timeSlot.getEndTime()
        );

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.TIME_SLOT_DELETED_EXCHANGE,
                RabbitMQConfig.TIME_SLOT_DELETED_ROUTING_KEY,
                event
        );

        System.out.println(">>> POSLAT EVENT: TimeSlotDeletedEventDTO, timeSlotId="
                + timeSlot.getId()
                + ", startTime=" + timeSlot.getStartTime()
                + ", endTime=" + timeSlot.getEndTime());
    }
}