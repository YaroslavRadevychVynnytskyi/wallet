package com.nerdysoft.axon.command;

import com.nerdysoft.model.enums.OperationType;
import com.nerdysoft.model.enums.ReserveType;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateBalanceCommand {
    @TargetAggregateIdentifier
    private Integer id;
    private ReserveType reserveType;
    private BigDecimal amount;
    private OperationType operationType;
}
