package com.nerdysoft.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableRabbit
public class RabbitConfig {
    public static final String TRANSACTION_QUEUE = "transaction-queue";
    public static final String TRANSACTION_EXCHANGE = "transaction-exchange";
    public static final String TRANSACTION_ROUTING_KEY = "transaction-key";

    @Bean
    public Queue transactionQueue() {
        return new Queue(TRANSACTION_QUEUE);
    }

    @Bean
    public Exchange transactionExchangeFanout() {
        return new DirectExchange(TRANSACTION_EXCHANGE);
    }

    @Bean
    public Binding transactionQueueBinding() {
        return BindingBuilder
                .bind(transactionQueue())
                .to(transactionExchangeFanout())
                .with(TRANSACTION_ROUTING_KEY)
                .noargs();
    }
}
