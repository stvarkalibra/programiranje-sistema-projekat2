package org.raflab.avanturaservice.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // ============================================================
    // 1) GUIDE -> AVANTURA
    // Kada se promeni rating vodiča, avantura-service ažurira cene
    // ============================================================

    public static final String GUIDE_RATING_QUEUE = "guide_rating_changed_queue";
    public static final String GUIDE_RATING_EXCHANGE = "guide_rating_changed_exchange";
    public static final String GUIDE_RATING_ROUTING_KEY = "guide.rating.changed";

    @Bean
    public Queue guideRatingChangedQueue() {
        return new Queue(GUIDE_RATING_QUEUE, true);
    }

    @Bean
    public TopicExchange guideRatingChangedExchange() {
        return new TopicExchange(GUIDE_RATING_EXCHANGE, true, false);
    }

    @Bean
    public Binding guideRatingChangedBinding() {
        return BindingBuilder
                .bind(guideRatingChangedQueue())
                .to(guideRatingChangedExchange())
                .with(GUIDE_RATING_ROUTING_KEY);
    }

    // ============================================================
    // 2) BOOKING -> AVANTURA
    // Kada booking-service obriše/cancel-uje rezervaciju,
    // avantura-service smanjuje currentOccupancy u TimeSlot-u
    // ============================================================

    public static final String AVAILABILITY_QUEUE = "availability_queue";
    public static final String AVAILABILITY_EXCHANGE = "availability_exchange";
    public static final String AVAILABILITY_ROUTING_KEY = "availability_routing_key";

    @Bean
    public Queue availabilityQueue() {
        return new Queue(AVAILABILITY_QUEUE, true);
    }

    @Bean
    public TopicExchange availabilityExchange() {
        return new TopicExchange(AVAILABILITY_EXCHANGE, true, false);
    }

    @Bean
    public Binding availabilityBinding() {
        return BindingBuilder
                .bind(availabilityQueue())
                .to(availabilityExchange())
                .with(AVAILABILITY_ROUTING_KEY);
    }

    // ============================================================
    // 3) AVANTURA -> BOOKING FEEDBACK
    // Avantura-service šalje običan String nazad booking-service-u:
    // "USPEŠNO: ..." ili "NEUSPEŠNO: ..."
    // Ovo mora da bude isto kao u booking-service RabbitMQConfig
    // ============================================================

    public static final String BOOKING_FEEDBACK_QUEUE = "booking_feedback_queue";
    public static final String BOOKING_FEEDBACK_EXCHANGE = "booking_feedback_exchange";
    public static final String BOOKING_FEEDBACK_KEY = "booking_feedback_key";

    @Bean
    public Queue bookingFeedbackQueue() {
        return new Queue(BOOKING_FEEDBACK_QUEUE, true);
    }

    @Bean
    public TopicExchange bookingFeedbackExchange() {
        return new TopicExchange(BOOKING_FEEDBACK_EXCHANGE, true, false);
    }

    @Bean
    public Binding bookingFeedbackBinding() {
        return BindingBuilder
                .bind(bookingFeedbackQueue())
                .to(bookingFeedbackExchange())
                .with(BOOKING_FEEDBACK_KEY);
    }

    // ============================================================
    // ZAJEDNIČKA RABBITMQ PODEŠAVANJA
    // ============================================================

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
                                         MessageConverter jsonMessageConverter) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter);
        return rabbitTemplate;
    }

    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }

    @Bean
    public ApplicationRunner initializeQueues(RabbitAdmin rabbitAdmin) {
        return args -> {
            rabbitAdmin.initialize();
            System.out.println(">>> AVANTURA RabbitMQ redovi/exchange/bindings su deklarisani.");
        };
    }
}