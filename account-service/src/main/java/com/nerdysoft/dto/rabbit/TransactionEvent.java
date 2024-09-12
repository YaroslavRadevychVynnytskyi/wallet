package com.nerdysoft.dto.rabbit;

import com.nerdysoft.dto.rabbit.enums.TransactionStatus;
import com.nerdysoft.dto.rabbit.enums.TransactionType;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class TransactionEvent implements Serializable {
    private UUID transactionId;
    private UUID fromWalletId;
    private UUID toWalletId;
    private BigDecimal amount;
    private String currency;
    private LocalDateTime timestamp;
    private TransactionStatus status;
    private TransactionType transactionType;
}
