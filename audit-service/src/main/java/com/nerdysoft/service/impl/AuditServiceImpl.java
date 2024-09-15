package com.nerdysoft.service.impl;

import com.nerdysoft.dto.LogResponseDto;
import com.nerdysoft.entity.activity.UserActivityEvent;
import com.nerdysoft.entity.activity.enums.Status;
import com.nerdysoft.entity.transaction.TransactionEvent;
import com.nerdysoft.repo.TransactionEventRepository;
import com.nerdysoft.repo.UserActivityEventRepository;
import com.nerdysoft.service.AuditService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuditServiceImpl implements AuditService {
    private final TransactionEventRepository transactionEventRepository;
    private final UserActivityEventRepository activityEventRepository;

    @Override
    public List<TransactionEvent> getAllTransactionLogs() {
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

    @Override
    public void saveUserActivityEvent(UserActivityEvent userActivityEvent) {
        activityEventRepository.save(userActivityEvent);
    }

    @Override
    public LogResponseDto logUserActivity(UserActivityEvent userActivityEvent) {
        activityEventRepository.save(userActivityEvent);
        return new LogResponseDto("User action logged successfully", Status.SUCCESS);
    }

    @Override
    public List<UserActivityEvent> getAllUserActivityLogs() {
        return activityEventRepository.findAll();
    }

    @Override
    public List<UserActivityEvent> getUserActivityLogsByUserId(UUID userID) {
        return activityEventRepository.findAllByUserId(userID);
    }
}
