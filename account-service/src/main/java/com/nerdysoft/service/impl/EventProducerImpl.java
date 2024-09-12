package com.nerdysoft.service.impl;

import com.nerdysoft.dto.rabbit.TransactionEvent;
import com.nerdysoft.service.EventProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EventProducerImpl implements EventProducer {
    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.transaction.exchange}")
    private String transactionExchange;

    @Value("${rabbitmq.transaction.routing_key}")
    private String transactionKey;

    @Override
    public void sendTransactionEvent(TransactionEvent event) {
        rabbitTemplate.convertAndSend(transactionExchange, transactionKey, event);
    }
}
