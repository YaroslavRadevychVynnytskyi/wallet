package com.nerdysoft.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nerdysoft.dto.event.Event;
import com.nerdysoft.dto.event.activity.UserActivityEvent;
import com.nerdysoft.dto.event.activity.enums.ActionType;
import com.nerdysoft.dto.event.activity.enums.EntityType;
import com.nerdysoft.dto.event.activity.enums.Status;
import com.nerdysoft.service.EventProducer;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EventProducerImpl implements EventProducer {
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    @Value("${rabbitmq.exchange}")
    private String exchange;

    @Value("${rabbitmq.routing-key.transaction-key}")
    private String transactionKey;

    @Value("${rabbitmq.routing-key.activity-key}")
    private String activityKey;

    @Override
    public <T extends Event> void sendEvent(T event) {
        rabbitTemplate.convertAndSend(exchange, transactionKey, event);
    }

    @Override
    public <T extends Event> void sendEvent(UUID accountID,
                                            UUID entityId,
                                            ActionType actionType,
                                            EntityType entityType,
                                            Optional<?> oldData,
                                            Optional<?> newData) {
        String oldDataJson;
        String newDataJson;

        try {
            oldDataJson = (oldData.isPresent()) ? objectMapper.writeValueAsString(oldData.get()) : null;
            newDataJson = (newData.isPresent()) ? objectMapper.writeValueAsString(newData.get()) : null;
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Can't write value as a string", e);
        }
        UserActivityEvent event = new UserActivityEvent(accountID, actionType, entityType, entityId,
            LocalDateTime.now(), oldDataJson, newDataJson, Status.SUCCESS);

        rabbitTemplate.convertAndSend(exchange, activityKey, event);
    }
}
