package com.nerdysoft.axon.event;

import com.nerdysoft.model.reserve.enums.ReserveType;
import com.nerdysoft.model.enums.OperationType;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.Data;

@Data
public class BalanceUpdatedEvent {
    private UUID id;
    private ReserveType reserveType;
    private BigDecimal amount;
    private OperationType operationType;
}
