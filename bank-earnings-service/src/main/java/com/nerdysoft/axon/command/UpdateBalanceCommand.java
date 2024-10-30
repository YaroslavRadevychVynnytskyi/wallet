package com.nerdysoft.axon.command;

import com.nerdysoft.entity.reserve.enums.ReserveType;
import com.nerdysoft.model.enums.OperationType;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateBalanceCommand {
    @TargetAggregateIdentifier
    private UUID id;
    private ReserveType reserveType;
    private BigDecimal amount;
    private OperationType operationType;
}
