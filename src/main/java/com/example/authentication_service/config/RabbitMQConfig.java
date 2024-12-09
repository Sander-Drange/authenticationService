package com.example.authentication_service.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.*;

@Configuration
public class RabbitMQConfig {

    public static final String USER_QUEUE = "user.queue";
    public static final String USER_EXCHANGE = "user.exchange";
    public static final String USER_ROUTING_KEY = "user.routingkey";

    @Bean
    public Queue queue() {
        return new Queue(USER_QUEUE);
    }

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(USER_EXCHANGE);
    }

    @Bean
    public Binding binding(Queue queue, TopicExchange exchange) {
        return BindingBuilder
                .bind(queue)
                .to(exchange)
                .with(USER_ROUTING_KEY);
    }
}
