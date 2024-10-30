package com.nerdysoft.axon.event.loanlimit;

import com.nerdysoft.model.enums.Currency;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Data;

@Data
public class TakeLoanLimitEvent {
    private UUID id;
    private UUID accountId;
    private String accountEmail;
    private UUID walletId;
    private BigDecimal availableAmount;
    private BigDecimal initialAmount;
    private boolean isRepaid;
    private Currency currency;
    private LocalDateTime timestamp;
    private LocalDateTime dueDate;
}
