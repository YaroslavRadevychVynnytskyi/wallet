package com.nerdysoft.axon.event.deposit;

import com.nerdysoft.model.enums.Currency;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.Data;

@Data
public class CancelWithdrawForDepositEvent {
    private UUID id;
    private UUID walletId;
    private BigDecimal amount;
    private Currency currency;
}
