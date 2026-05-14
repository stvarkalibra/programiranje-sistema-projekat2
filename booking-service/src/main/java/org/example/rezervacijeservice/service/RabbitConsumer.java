package org.example.rezervacijeservice.service;
import org.example.rezervacijeservice.config.RabbitMQConfig;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class RabbitConsumer {

    @RabbitListener(queues = RabbitMQConfig.QUEUE)
    public void consume(String message) {
        System.out.println("LOG IZ SERVISA B: Primljena poruka -> " + message);
    }
}
