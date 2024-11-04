package com.nerdysoft.dto.event.transaction;

import com.nerdysoft.dto.event.Event;
import com.nerdysoft.dto.event.transaction.enums.TransactionType;
import com.nerdysoft.model.enums.TransactionStatus;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
public class TransactionEvent extends Event implements Serializable {
    private UUID transactionId;
    private UUID fromWalletId;
    private UUID toWalletId;
    private BigDecimal amount;
    private String currency;
    private LocalDateTime timestamp;
    private TransactionStatus status;
    private TransactionType transactionType;
}
