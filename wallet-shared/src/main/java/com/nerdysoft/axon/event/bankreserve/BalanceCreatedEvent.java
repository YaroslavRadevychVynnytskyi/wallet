package com.nerdysoft.axon.event.bankreserve;

import com.nerdysoft.model.enums.ReserveType;
import java.math.BigDecimal;
import lombok.Data;

@Data
public class BalanceCreatedEvent {
    private Integer id;
    private ReserveType type;
    private BigDecimal totalFunds;
}
