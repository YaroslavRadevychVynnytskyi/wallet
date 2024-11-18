package com.nerdysoft.axon.event.deposit;

import com.nerdysoft.model.enums.Currency;
import com.nerdysoft.model.enums.OperationType;
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
public class UpdateWalletBalanceCommand {
    @TargetAggregateIdentifier
    private UUID id;
    private UUID walletId;
    private BigDecimal amount;
    private Currency currency;
    private OperationType operationType;
}
