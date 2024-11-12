package com.nerdysoft.axon.command.deposit;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FailedDepositCommand {
    @TargetAggregateIdentifier
    private UUID id;
    private UUID accountId;
    private String reason;
    private LocalDateTime timestamp;
}
