package com.nerdysoft.dto.feign;

import com.nerdysoft.model.enums.Currency;
import com.nerdysoft.model.enums.TransactionStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Setter;

@Setter
public class Transaction {
    private UUID transactionId;
    private UUID walletId;
    private UUID toWalletId;
    private BigDecimal walletBalance;
    private BigDecimal amount;
    private Currency currency;
    private TransactionStatus status;
    private LocalDateTime createdAt;

    public UUID transactionId() {
        return transactionId;
    }

    public UUID walletId() {
        return walletId;
    }

    public UUID toWalletId() {
        return toWalletId;
    }

    public BigDecimal walletBalance() {
        return walletBalance;
    }

    public BigDecimal amount() {
        return amount;
    }

    public Currency currency() {
        return currency;
    }

    public TransactionStatus status() {
        return status;
    }

    public LocalDateTime createdAt() {
        return createdAt;
    }
}