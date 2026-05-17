package org.raflab.guideservice.config;

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
            System.out.println(">>> GUIDE RabbitMQ redovi/exchange/binding su deklarisani.");
        };
    }
}