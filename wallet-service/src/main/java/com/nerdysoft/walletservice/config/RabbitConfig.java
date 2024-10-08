package com.nerdysoft.walletservice.config;

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

    @Value("${rabbitmq.queue.commission-queue}")
    private String commissionQueue;

    @Value("${rabbitmq.routing-key.commission-key}")
    private String commissionKey;

    @Bean
    public Exchange exchange() {
        return new DirectExchange(exchange);
    }

    @Bean
    public Queue commissionQueue() {
        return new Queue(commissionQueue);
    }

    @Bean
    public Binding commissionQueueBinding() {
        return BindingBuilder
                .bind(commissionQueue())
                .to(exchange())
                .with(commissionKey)
                .noargs();
    }
}
