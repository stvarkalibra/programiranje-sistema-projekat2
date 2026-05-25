package org.raflab.avanturaservice.messaging;

import org.raflab.avanturaservice.config.RabbitMQConfig;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class BookingFeedbackListener {

    @RabbitListener(queues = RabbitMQConfig.BOOKING_FEEDBACK_QUEUE)
    public void handleBookingFeedback(String feedbackMessage) {
        System.out.println(">>> AVANTURA PRIMILA FEEDBACK OD BOOKING-SERVICE: " + feedbackMessage);
    }
}