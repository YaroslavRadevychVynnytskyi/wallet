package com.nerdysoft.controller;

import com.nerdysoft.axon.query.transaction.FindAllTransactionsByWalletIdQuery;
import com.nerdysoft.entity.Transaction;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class TransactionController {
    private final QueryGateway queryGateway;

    @GetMapping("/{walletId}")
    public ResponseEntity<List<Transaction>> findAllTransactionsByWalletId(@PathVariable UUID walletId) {
        return ResponseEntity.ok(queryGateway.query(new FindAllTransactionsByWalletIdQuery(walletId),
            ResponseTypes.multipleInstancesOf(Transaction.class)).join());
    }
}
