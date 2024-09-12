package com.nerdysoft.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableRabbit
public class RabbitConfig {
    @Value("${rabbitmq.transaction.queue}")
    private String transactionQueue;

    @Value("${rabbitmq.transaction.exchange}")
    private String transactionExchange;

    @Value("${rabbitmq.transaction.routing_key}")
    private String transactionKey;

    @Bean
    public Queue transactionQueue() {
        return new Queue(transactionQueue);
    }

    @Bean
    public Exchange transactionExchange() {
        return new DirectExchange(transactionExchange);
    }

    @Bean
    public Binding transactionQueueBinding() {
        return BindingBuilder
                .bind(transactionQueue())
                .to(transactionExchange())
                .with(transactionKey)
                .noargs();
    }
}
