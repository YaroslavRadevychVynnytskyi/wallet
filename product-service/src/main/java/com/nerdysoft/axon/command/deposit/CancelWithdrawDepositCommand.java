package com.nerdysoft.axon.command.deposit;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CancelWithdrawDepositCommand {
    @TargetAggregateIdentifier
    private UUID id;
}
