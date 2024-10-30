package com.nerdysoft.axon.command.loanlimit;

import com.nerdysoft.dto.feign.enums.Currency;
import java.util.UUID;
import lombok.Builder;
import lombok.Data;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Data
@Builder
public class TakeLoanLimitCommand {
    @TargetAggregateIdentifier
    private UUID id;
    private UUID accountId;
    private String accountEmail;
    private Currency currency;
}
