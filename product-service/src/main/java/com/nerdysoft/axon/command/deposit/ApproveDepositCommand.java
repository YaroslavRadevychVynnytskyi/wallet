package com.nerdysoft.axon.command.deposit;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Data
@AllArgsConstructor
public class ApproveDepositCommand {
    @TargetAggregateIdentifier
    private UUID id;
}
