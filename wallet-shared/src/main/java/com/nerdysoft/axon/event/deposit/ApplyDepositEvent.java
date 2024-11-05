package com.nerdysoft.axon.event.deposit;

import com.nerdysoft.model.enums.Currency;
import com.nerdysoft.model.enums.DepositStatus;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import lombok.Data;

@Data
public class ApplyDepositEvent {
    private UUID id;
    private UUID accountId;
    private String accountEmail;
    private UUID walletId;
    private BigDecimal amount;
    private Currency currency;
    private LocalDate depositDate;
    private LocalDate maturityDate;
    private LocalDate notificationDate;
    private BigDecimal yearInterestRate;
    private BigDecimal fourMonthsInterestRate;
    private DepositStatus depositStatus;
}
