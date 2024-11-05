package com.nerdysoft.axon.event.loanlimit;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RepayLoanLimitEvent {
    private UUID accountId;
}
