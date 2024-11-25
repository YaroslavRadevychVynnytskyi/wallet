package com.nerdysoft.axon.command.bankearnings;

import com.nerdysoft.model.enums.OperationType;
import com.nerdysoft.model.enums.ReserveType;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateBankReserveCommand {
    private UUID id;
    @TargetAggregateIdentifier
    private ReserveType reserveType;
    private BigDecimal amount;
    private OperationType operationType;
}