package com.nerdysoft.axon.event.bankreserve;

import com.nerdysoft.model.enums.OperationType;
import com.nerdysoft.model.enums.ReserveType;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.Data;

@Data
public class BalanceUpdatedEvent {
    private UUID id;
    private ReserveType reserveType;
    private BigDecimal balance;
    private BigDecimal amount;
    private OperationType operationType;
}
