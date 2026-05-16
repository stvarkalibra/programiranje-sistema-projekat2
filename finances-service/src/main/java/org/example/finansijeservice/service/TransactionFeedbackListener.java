package org.example.finansijeservice.service;

import lombok.AllArgsConstructor;
import org.example.finansijeservice.repository.TransactionsRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class TransactionFeedbackListener {
    private final TransactionsRepository transactionsRepository;


    @RabbitListener(queues = "transaction_feedback_queue")
    public void handleFeedback(String message) {
        System.out.println(message);

    }

}
