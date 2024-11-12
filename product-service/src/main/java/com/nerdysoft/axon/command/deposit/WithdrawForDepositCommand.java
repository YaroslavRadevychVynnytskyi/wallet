package com.nerdysoft.axon.command.deposit;

import com.nerdysoft.model.enums.Currency;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.Builder;
import lombok.Data;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Data
@Builder
public class WithdrawForDepositCommand {
    @TargetAggregateIdentifier
    private UUID id;
    private UUID walletId;
    private BigDecimal amount;
    private Currency currency;
}
