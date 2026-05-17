package org.raflab.avanturaservice.messaging;

import lombok.RequiredArgsConstructor;
import org.raflab.avanturaservice.config.RabbitMQConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookingFeedbackProducer {

    private final RabbitTemplate rabbitTemplate;

    public void sendFeedback(String message) {
        System.out.println(">>> AVANTURA šalje feedback ka BOOKING: " + message);

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.BOOKING_FEEDBACK_EXCHANGE,
                RabbitMQConfig.BOOKING_FEEDBACK_KEY,
                message
        );
    }
}