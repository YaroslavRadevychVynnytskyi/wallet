package com.nerdysoft.dto.event.transaction;

import com.nerdysoft.dto.event.Event;
import com.nerdysoft.dto.event.transaction.enums.TransactionStatus;
import com.nerdysoft.dto.event.transaction.enums.TransactionType;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Data;

@Data
public class TransactionEvent implements Event, Serializable {
    private UUID transactionId;
    private UUID fromWalletId;
    private UUID toWalletId;
    private BigDecimal amount;
    private String currency;
    private LocalDateTime timestamp;
    private TransactionStatus status;
    private TransactionType transactionType;
}
