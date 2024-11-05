package com.nerdysoft.axon.command.loan;

import java.util.UUID;
import lombok.Value;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Value
public class RepayLoanCommand {
    @TargetAggregateIdentifier
    UUID id;
    UUID accountId;
}
