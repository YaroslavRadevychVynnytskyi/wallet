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
    @Value("${rabbitmq.exchange}")
    private String exchange;

    @Value("${rabbitmq.queue.transaction-queue}")
    private String transactionQueue;

    @Value("${rabbitmq.routing-key.transaction-key}")
    private String transactionKey;

    @Value("${rabbitmq.queue.activity-queue}")
    private String activityQueue;

    @Value("${rabbitmq.routing-key.activity-key}")
    private String activityKey;

    @Bean
    public Exchange exchange() {
        return new DirectExchange(exchange);
    }

    @Bean
    public Queue transactionQueue() {
        return new Queue(transactionQueue);
    }

    @Bean Queue activityQueue() {
        return new Queue(activityQueue);
    }

    @Bean
    public Binding transactionQueueBinding() {
        return BindingBuilder
                .bind(transactionQueue())
                .to(exchange())
                .with(transactionKey)
                .noargs();
    }

    @Bean
    public Binding activityQueueBinding() {
        return BindingBuilder
                .bind(activityQueue())
                .to(exchange())
                .with(activityKey)
                .noargs();
    }
}
