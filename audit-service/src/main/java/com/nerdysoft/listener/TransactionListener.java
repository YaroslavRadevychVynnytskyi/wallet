package com.nerdysoft.listener;

import com.nerdysoft.config.RabbitConfig;
import com.nerdysoft.entity.TransactionEvent;
import com.nerdysoft.service.AuditService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TransactionListener {
    private final AuditService auditService;

    @RabbitListener(queues = RabbitConfig.TRANSACTION_QUEUE)
    public void saveTransaction(TransactionEvent transactionEvent) {
        auditService.saveTransactionEvent(transactionEvent);
    }
}
