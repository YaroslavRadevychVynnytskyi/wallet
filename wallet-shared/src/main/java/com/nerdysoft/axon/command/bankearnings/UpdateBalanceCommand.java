package com.nerdysoft.axon.command.bankearnings;

import com.nerdysoft.model.enums.OperationType;
import com.nerdysoft.model.enums.ReserveType;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateBalanceCommand {
    @TargetAggregateIdentifier
    private ReserveType reserveType;
    private BigDecimal amount;
    private OperationType operationType;
}
