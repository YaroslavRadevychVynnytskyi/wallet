package com.nerdysoft.service;

import com.nerdysoft.entity.TransactionEvent;
import java.util.List;
import java.util.UUID;

public interface AuditService {
    List<TransactionEvent> getAll();

    TransactionEvent getByTransactionId(UUID transactionId);

    void saveTransactionEvent(TransactionEvent transactionEvent);
}
