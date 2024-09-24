package com.nerdysoft.listener;

import com.nerdysoft.entity.activity.UserActivityEvent;
import com.nerdysoft.entity.transaction.TransactionEvent;
import com.nerdysoft.service.AuditService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EventListener {
    private final AuditService auditService;

    @RabbitListener(queues = "${rabbitmq.queue.transaction-queue}")
    public void listenAndSaveTransactionEvent(Message<TransactionEvent> message) {
        auditService.saveTransactionEvent(message.getPayload());
    }

    @RabbitListener(queues = "${rabbitmq.queue.activity-queue}")
    public void listenAndSaveActivityEvent(Message<UserActivityEvent> message) {
        auditService.saveUserActivityEvent(message.getPayload());
    }
}
