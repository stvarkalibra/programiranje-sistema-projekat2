package org.example.rezervacijeservice.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    public static final String QUEUE = "transaction_queue";
    public static final String EXCHANGE = "transaction_exchange";
    public static final String ROUTING_KEY = "transaction_routing_key";

    @Bean
    public Queue queue() { return new Queue(QUEUE); }

    @Bean
    public TopicExchange exchange() { return new TopicExchange(EXCHANGE); }

    @Bean
    public Binding binding(Queue queue, TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(ROUTING_KEY);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }

    // Kreiranje exchange, queues and binding prilikom pokretanja aplikacije
    @Bean
    public ApplicationRunner initializeQueues(RabbitAdmin rabbitAdmin) {
        return args -> {
            rabbitAdmin.initialize();
            System.out.println(">>> RABBITMQ REDOVI SU DEKLARISANI I SPREMNI!");
        };
    }


    public static final String AVAILABILITY_QUEUE = "availability_queue";
    public static final String AVAILABILITY_EXCHANGE = "availability_exchange";
    public static final String AVAILABILITY_ROUTING_KEY = "availability_routing_key";

    @Bean
    public Queue availabilityQueue() { return new Queue(AVAILABILITY_QUEUE); }

    @Bean
    public TopicExchange availabilityExchange() { return new TopicExchange(AVAILABILITY_EXCHANGE); }

    @Bean
    public Binding availabilityBinding() {
        return BindingBuilder.bind(availabilityQueue()).to(availabilityExchange()).with(AVAILABILITY_ROUTING_KEY);
    }

    public static final String BOOKING_FEEDBACK_QUEUE = "booking_feedback_queue";
    public static final String BOOKING_FEEDBACK_EXCHANGE = "booking_feedback_exchange";
    public static final String BOOKING_FEEDBACK_KEY = "booking_feedback_key";

    @Bean
    public Queue bookingFeedbackQueue() {
        return new Queue(BOOKING_FEEDBACK_QUEUE);
    }

    @Bean
    public TopicExchange bookingFeedbackExchange() {
        return new TopicExchange(BOOKING_FEEDBACK_EXCHANGE);
    }

    @Bean
    public Binding bookingFeedbackBinding(Queue bookingFeedbackQueue, TopicExchange bookingFeedbackExchange) {
        return BindingBuilder.bind(bookingFeedbackQueue).to(bookingFeedbackExchange).with(BOOKING_FEEDBACK_KEY);
    }

}
