package com.nerdysoft.axon.command;

import com.nerdysoft.entity.reserve.enums.ReserveType;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.Data;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Data
public class CreateBalanceCommand {
    @TargetAggregateIdentifier
    private UUID id;
    private ReserveType reserveType;
    private BigDecimal amount;
}
