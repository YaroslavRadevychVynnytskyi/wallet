package com.nerdysoft.controller;

import com.nerdysoft.entity.Transaction;
import com.nerdysoft.service.TransactionService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class TransactionController {
    private final TransactionService transactionService;

    @GetMapping("/{walletId}")
    public ResponseEntity<List<Transaction>> getTransactionsByWalletId(@PathVariable UUID walletId) {
        return ResponseEntity.ok(transactionService.getAllByWalletId(walletId));
    }
}
