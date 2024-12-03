package com.nerdysoft.axon.event.wallet;

import com.nerdysoft.model.enums.Currency;
import com.nerdysoft.model.enums.OperationType;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.Data;

@Data
public class UpdateWalletBalanceEvent {
    private UUID id;
    private UUID walletId;
    private BigDecimal amount;
    private Currency currency;
    private OperationType operationType;
}
