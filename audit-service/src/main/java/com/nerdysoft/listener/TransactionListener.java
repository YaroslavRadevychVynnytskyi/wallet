package com.nerdysoft.listener;

import com.nerdysoft.entity.TransactionEvent;
import com.nerdysoft.service.AuditService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TransactionListener {
    private final AuditService auditService;

    @RabbitListener(queues = "${rabbitmq.transaction.queue}")
    public void listenAndSaveTransaction(Message<TransactionEvent> message) {
        auditService.saveTransactionEvent(message.getPayload());
    }
}
