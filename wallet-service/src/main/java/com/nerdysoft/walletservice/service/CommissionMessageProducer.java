package com.nerdysoft.walletservice.service;

import com.nerdysoft.walletservice.dto.rabbit.CommissionRequestMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommissionMessageProducer {
    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange}")
    private String exchange;

    @Value("${rabbitmq.routing-key.commission-key}")
    private String commissionKey;

    public void sendMessage(CommissionRequestMessage message) {
        rabbitTemplate.convertAndSend(exchange, commissionKey, message);
    }
}
