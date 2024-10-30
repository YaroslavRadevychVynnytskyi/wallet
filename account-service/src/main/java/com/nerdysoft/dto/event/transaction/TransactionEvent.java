package com.nerdysoft.dto.event.transaction;

import com.nerdysoft.dto.event.Event;
import com.nerdysoft.dto.event.transaction.enums.TransactionType;
import com.nerdysoft.model.enums.TransactionStatus;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TransactionEvent extends Event implements Serializable {
    private final UUID transactionId;
    private final UUID fromWalletId;
    private final UUID toWalletId;
    private final BigDecimal amount;
    private final String currency;
    private final LocalDateTime timestamp;
    private final TransactionStatus status;
    private final TransactionType transactionType;
}
