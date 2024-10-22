package com.nerdysoft.service;

import com.nerdysoft.dto.response.LogResponseDto;
import com.nerdysoft.entity.activity.UserActivityEvent;
import com.nerdysoft.entity.transaction.TransactionEvent;
import java.util.List;
import java.util.UUID;

public interface AuditService {
    List<TransactionEvent> getAllTransactionLogs();

    TransactionEvent getByTransactionId(UUID transactionId);

    void saveTransactionEvent(TransactionEvent transactionEvent);

    void saveUserActivityEvent(UserActivityEvent userActivityEvent);

    LogResponseDto logUserActivity(UserActivityEvent userActivityEvent);

    List<UserActivityEvent> getAllUserActivityLogs();

    List<UserActivityEvent> getUserActivityLogsByUserId(UUID userId);
}
