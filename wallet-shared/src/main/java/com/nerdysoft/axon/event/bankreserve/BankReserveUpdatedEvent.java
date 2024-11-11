package com.nerdysoft.axon.event.bankreserve;

import com.nerdysoft.model.enums.OperationType;
import com.nerdysoft.model.enums.ReserveType;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.Data;

@Data
public class BankReserveUpdatedEvent {
    private UUID id;
    private UUID reserveId;
    private ReserveType reserveType;
    private BigDecimal amount;
    private OperationType operationType;
}
