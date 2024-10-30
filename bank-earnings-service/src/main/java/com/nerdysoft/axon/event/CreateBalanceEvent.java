package com.nerdysoft.axon.event;

import com.nerdysoft.entity.reserve.enums.ReserveType;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.Data;

@Data
public class CreateBalanceEvent {
    private UUID id;
    private ReserveType type;
    private BigDecimal totalFunds;
}
