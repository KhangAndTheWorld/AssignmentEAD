package com.t2308e.assignment.logservice.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabitMQConfig {
    public static final String EXCHANGE_NAME = "ecommerce.exchange";
    public static final String QUEUE_NAME = "ecommerce.log.queue";

    @Bean
    public FanoutExchange fanoutExchange() {
        return new FanoutExchange(EXCHANGE_NAME);
    }

    @Bean
    public Queue logQueue() {
        return new Queue(QUEUE_NAME, true);
    }

    @Bean
    public Binding binding(FanoutExchange fanoutExchange, Queue logQueue) {
        return BindingBuilder.bind(logQueue).to(fanoutExchange);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        return new RabbitTemplate(connectionFactory);
    }
}
