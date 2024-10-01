package com.nerdysoft.model.transaction;

import com.nerdysoft.model.transaction.enums.TransactionStatus;
import com.nerdysoft.model.transaction.enums.TransactionType;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "transaction_events")
@Getter
@Setter
@EqualsAndHashCode(of = "transactionId")
public class TransactionEvent {
    private UUID transactionId;
    private UUID fromWalletId;
    private UUID toWalletId;
    private BigDecimal amount;
    private String currency;
    private LocalDateTime timestamp;
    private TransactionStatus status;
    private TransactionType transactionType;
}
