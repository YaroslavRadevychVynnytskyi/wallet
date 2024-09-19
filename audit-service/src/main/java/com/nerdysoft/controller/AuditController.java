package com.nerdysoft.controller;

import com.nerdysoft.dto.LogResponseDto;
import com.nerdysoft.entity.activity.UserActivityEvent;
import com.nerdysoft.entity.transaction.TransactionEvent;
import com.nerdysoft.service.AuditService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/audit")
@RequiredArgsConstructor
public class AuditController {
    private final AuditService auditService;

    @GetMapping("/transactions")
    public ResponseEntity<List<TransactionEvent>> getAllTransactionLogs() {
        return ResponseEntity.ok(auditService.getAllTransactionLogs());
    }

    @GetMapping("/transactions/{transactionId}")
    public ResponseEntity<TransactionEvent> getTransactionLogById(@PathVariable UUID transactionId) {
        return ResponseEntity.ok(auditService.getByTransactionId(transactionId));
    }

    @PostMapping("/user-activity")
    public ResponseEntity<LogResponseDto> logUserActivity(
            @RequestBody UserActivityEvent userActivityEvent) {
        return ResponseEntity.ok(auditService.logUserActivity(userActivityEvent));
    }

    @GetMapping("/user-activity")
    public ResponseEntity<List<UserActivityEvent>> getAllUserActivityLogs() {
        return ResponseEntity.ok(auditService.getAllUserActivityLogs());
    }

    @GetMapping("/user-activity/{userId}")
    public ResponseEntity<List<UserActivityEvent>> getUserActivityLogsByUserId(
            @PathVariable UUID userId) {
        return ResponseEntity.ok(auditService.getUserActivityLogsByUserId(userId));
    }
}
