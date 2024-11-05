package com.nerdysoft.axon.event.bankreserve;

import com.nerdysoft.model.enums.ReserveType;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.Data;

@Data
public class BalanceCreatedEvent {
    private UUID id;
    private ReserveType type;
    private BigDecimal totalFunds;
}
