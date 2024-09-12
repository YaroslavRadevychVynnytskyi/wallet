package com.nerdysoft.service.impl;

import com.nerdysoft.entity.TransactionEvent;
import com.nerdysoft.repo.TransactionEventRepository;
import com.nerdysoft.service.AuditService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuditServiceImpl implements AuditService {
    private final TransactionEventRepository transactionEventRepository;

    @Override
    public List<TransactionEvent> getAll() {
        return transactionEventRepository.findAll();
    }

    @Override
    public TransactionEvent getByTransactionId(UUID transactionId) {
        return transactionEventRepository.findByTransactionId(transactionId).orElseThrow();
    }

    @Override
    public void saveTransactionEvent(TransactionEvent transactionEvent) {
        transactionEventRepository.save(transactionEvent);
    }
}
