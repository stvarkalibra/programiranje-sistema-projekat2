package com.ecotravel.accommodation.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // ============================================================
    // EXCHANGE-ovi
    // ============================================================

    /** Accommodation publishuje događaje na ovaj exchange */
    public static final String ACCOMMODATION_EXCHANGE = "accommodation.events";

    /** Maintenance publishuje događaje na ovaj exchange (accommodation sluša) */
    public static final String MAINTENANCE_EXCHANGE = "maintenance.events";

    /** Booking publishuje događaje (accommodation NE sluša direktno, ali je deklarisan radi konzistencije) */
    public static final String BOOKING_EXCHANGE = "booking.events";

    // ============================================================
    // ROUTING KEYS koje accommodation publishuje
    // ============================================================
    public static final String UNIT_CREATED_ROUTING_KEY = "unit.created";
    public static final String UNIT_DELETED_ROUTING_KEY = "unit.deleted";

    // ============================================================
    // ROUTING KEYS koje accommodation PRIMA (od maintenance)
    // ============================================================
    public static final String WORK_ORDER_COMPLETED_ROUTING_KEY = "workorder.completed";

    // ============================================================
    // QUEUE-ovi koje accommodation CONSUMES
    // ============================================================
    public static final String ACCOMMODATION_WORK_ORDER_COMPLETED_QUEUE =
            "accommodation.workorder.completed.queue";

    // ============================================================
    // Exchange deklaracije
    // ============================================================

    @Bean
    public TopicExchange accommodationExchange() {
        // durable=true: exchange preživljava restart brokera
        return ExchangeBuilder
                .topicExchange(ACCOMMODATION_EXCHANGE)
                .durable(true)
                .build();
    }

    @Bean
    public TopicExchange maintenanceExchange() {
        return ExchangeBuilder
                .topicExchange(MAINTENANCE_EXCHANGE)
                .durable(true)
                .build();
    }

    // ============================================================
    // Queue deklaracije (accommodation kao consumer)
    // ============================================================

    /**
     * Queue na kojoj accommodation čeka obavještenje da je radni nalog završen.
     * Kad maintenance završi nalog, accommodation briše MAINTENANCE blokadu iz kalendara.
     * durable=true: poruke preživljavaju restart brokera.
     */
    @Bean
    public Queue accommodationWorkOrderCompletedQueue() {
        return QueueBuilder
                .durable(ACCOMMODATION_WORK_ORDER_COMPLETED_QUEUE)
                .build();
    }

    // ============================================================
    // Binding-i
    // ============================================================

    @Bean
    public Binding workOrderCompletedBinding(
            Queue accommodationWorkOrderCompletedQueue,
            TopicExchange maintenanceExchange) {
        return BindingBuilder
                .bind(accommodationWorkOrderCompletedQueue)
                .to(maintenanceExchange)
                .with(WORK_ORDER_COMPLETED_ROUTING_KEY);
    }

    // ============================================================
    // Serialization — JSON umjesto Java serijalizacije
    // ============================================================

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }
}