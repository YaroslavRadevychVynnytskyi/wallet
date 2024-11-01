package com.nerdysoft.axon.event.bankreserve;

import com.nerdysoft.model.enums.OperationType;
import com.nerdysoft.model.enums.ReserveType;
import java.math.BigDecimal;
import lombok.Data;

@Data
public class BalanceUpdatedEvent {
    private Integer id;
    private ReserveType reserveType;
    private BigDecimal amount;
    private OperationType operationType;
}
