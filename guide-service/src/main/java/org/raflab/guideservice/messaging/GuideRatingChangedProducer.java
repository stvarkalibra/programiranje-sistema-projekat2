package org.raflab.guideservice.messaging;

import lombok.RequiredArgsConstructor;
import org.example.sharedmodule.GuideRatingChangedEventDTO;
import org.raflab.guideservice.config.RabbitMQConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GuideRatingChangedProducer {

    private final RabbitTemplate rabbitTemplate;

    public void sendGuideRatingChangedEvent(GuideRatingChangedEventDTO event) {
        System.out.println(">>> GUIDE šalje event u RabbitMQ: " + event);

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.GUIDE_RATING_EXCHANGE,
                RabbitMQConfig.GUIDE_RATING_ROUTING_KEY,
                event
        );
    }
}