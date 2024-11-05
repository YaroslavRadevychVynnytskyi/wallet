package com.nerdysoft.axon.command.loanlimit;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Data
@AllArgsConstructor
public class RepayLoanLimitCommand {
    @TargetAggregateIdentifier
    private UUID id;
    private UUID accountId;
}
