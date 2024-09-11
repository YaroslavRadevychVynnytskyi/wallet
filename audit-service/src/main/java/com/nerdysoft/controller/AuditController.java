package com.nerdysoft.controller;

import com.nerdysoft.entity.TransactionEvent;
import com.nerdysoft.service.AuditService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/audit")
@RequiredArgsConstructor
public class AuditController {
    private final AuditService auditService;

    @GetMapping("/transactions")
    public ResponseEntity<List<TransactionEvent>> getAll() {
        return ResponseEntity.ok(auditService.getAll());
    }

    @GetMapping("/transactions/{transactionId}")
    public ResponseEntity<TransactionEvent> getById(@PathVariable UUID transactionId) {
        return ResponseEntity.ok(auditService.getById(transactionId));
    }
}
