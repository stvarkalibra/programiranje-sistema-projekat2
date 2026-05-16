package org.example.rezervacijeservice.service;

import org.springframework.amqp.rabbit.annotation.RabbitListener;

public class CancelledBookingFeedbackListener {

    @RabbitListener(queues = "booking_feedback_queue")
    public void handleFeedback(String message) {
        System.out.println(message);
    }

}
