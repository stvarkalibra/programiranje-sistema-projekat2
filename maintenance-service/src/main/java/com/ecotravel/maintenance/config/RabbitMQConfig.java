package com.ecotravel.maintenance.config;

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
    // EXCHANGE-ovi (isti nazivi kao u accommodation-service!)
    // ============================================================
    public static final String ACCOMMODATION_EXCHANGE = "accommodation.events";
    public static final String MAINTENANCE_EXCHANGE = "maintenance.events";
    public static final String BOOKING_EXCHANGE = "booking.events";

    // ============================================================
    // ROUTING KEYS koje maintenance publishuje
    // ============================================================
    public static final String WORK_ORDER_COMPLETED_ROUTING_KEY = "workorder.completed";

    // ============================================================
    // ROUTING KEYS koje maintenance prima
    // ============================================================
    public static final String UNIT_CREATED_ROUTING_KEY = "unit.created";
    public static final String UNIT_DELETED_ROUTING_KEY = "unit.deleted";
    public static final String BOOKING_CANCELLED_ROUTING_KEY = "booking.cancelled"; // od booking-service

    // ============================================================
    // QUEUE-ovi koje maintenance consumes
    // ============================================================
    public static final String MAINTENANCE_UNIT_CREATED_QUEUE = "maintenance.unit.created.queue";
    public static final String MAINTENANCE_UNIT_DELETED_QUEUE = "maintenance.unit.deleted.queue";
    public static final String MAINTENANCE_BOOKING_CANCELLED_QUEUE = "maintenance.booking.cancelled.queue";

    // ============================================================
    // Exchange deklaracije
    // ============================================================

    @Bean
    public TopicExchange accommodationExchange() {
        return ExchangeBuilder.topicExchange(ACCOMMODATION_EXCHANGE).durable(true).build();
    }

    @Bean
    public TopicExchange maintenanceExchange() {
        return ExchangeBuilder.topicExchange(MAINTENANCE_EXCHANGE).durable(true).build();
    }

    @Bean
    public TopicExchange bookingExchange() {
        return ExchangeBuilder.topicExchange(BOOKING_EXCHANGE).durable(true).build();
    }

    // ============================================================
    // Queue deklaracije (maintenance kao consumer)
    // ============================================================

    /**
     * Queue za primanje obavještenja o novoj smještajnoj jedinici.
     * Maintenance loguje novu jedinicu radi praćenja istorije.
     */
    @Bean
    public Queue maintenanceUnitCreatedQueue() {
        return QueueBuilder.durable(MAINTENANCE_UNIT_CREATED_QUEUE).build();
    }

    /**
     * Queue za primanje obavještenja o brisanju smještajne jedinice.
     * Maintenance automatski otkazuje sve PENDING naloge za tu jedinicu.
     */
    @Bean
    public Queue maintenanceUnitDeletedQueue() {
        return QueueBuilder.durable(MAINTENANCE_UNIT_DELETED_QUEUE).build();
    }

    /**
     * Queue za primanje BookingCancelled od booking-service.
     * Maintenance automatski kreira CLEANING nalog kad se rezervacija otkaže.
     * Ovo je komunikacija sa tuđim servisom — ispunjava uslov zadatka.
     */
    @Bean
    public Queue maintenanceBookingCancelledQueue() {
        return QueueBuilder.durable(MAINTENANCE_BOOKING_CANCELLED_QUEUE).build();
    }

    // ============================================================
    // Binding-i
    // ============================================================

    @Bean
    public Binding unitCreatedBinding(Queue maintenanceUnitCreatedQueue,
                                      TopicExchange accommodationExchange) {
        return BindingBuilder
                .bind(maintenanceUnitCreatedQueue)
                .to(accommodationExchange)
                .with(UNIT_CREATED_ROUTING_KEY);
    }

    @Bean
    public Binding unitDeletedBinding(Queue maintenanceUnitDeletedQueue,
                                      TopicExchange accommodationExchange) {
        return BindingBuilder
                .bind(maintenanceUnitDeletedQueue)
                .to(accommodationExchange)
                .with(UNIT_DELETED_ROUTING_KEY);
    }

    @Bean
    public Binding bookingCancelledBinding(Queue maintenanceBookingCancelledQueue,
                                           TopicExchange bookingExchange) {
        return BindingBuilder
                .bind(maintenanceBookingCancelledQueue)
                .to(bookingExchange)
                .with(BOOKING_CANCELLED_ROUTING_KEY);
    }

    // ============================================================
    // Serialization
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