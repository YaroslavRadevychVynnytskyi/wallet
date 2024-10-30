package com.nerdysoft.axon.event;

import com.nerdysoft.entity.reserve.enums.ReserveType;
import com.nerdysoft.model.enums.OperationType;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.Data;

@Data
public class UpdateBalanceEvent {
    private UUID id;
    private ReserveType reserveType;
    private BigDecimal amount;
    private OperationType operationType;
}
