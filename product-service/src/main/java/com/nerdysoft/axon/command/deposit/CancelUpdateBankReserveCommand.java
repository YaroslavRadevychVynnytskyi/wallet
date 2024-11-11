package com.nerdysoft.axon.command.deposit;

import com.nerdysoft.model.enums.OperationType;
import com.nerdysoft.model.enums.ReserveType;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.Builder;
import lombok.Data;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Data
@Builder
public class CancelUpdateBankReserveCommand {
    @TargetAggregateIdentifier
    private UUID id;
    private UUID reserveId;
    private ReserveType reserveType;
    private BigDecimal amount;
    private OperationType operationType;
    private String reason;
}
