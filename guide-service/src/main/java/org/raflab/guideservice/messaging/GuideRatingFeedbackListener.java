package org.raflab.guideservice.messaging;

import org.raflab.guideservice.config.RabbitMQConfig;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class GuideRatingFeedbackListener {

    @RabbitListener(queues = RabbitMQConfig.GUIDE_RATING_FEEDBACK_QUEUE)
    public void handleGuideRatingFeedback(String feedbackMessage) {
        System.out.println(">>> GUIDE PRIMIO FEEDBACK OD AVANTURA-SERVICE: " + feedbackMessage);
    }
}