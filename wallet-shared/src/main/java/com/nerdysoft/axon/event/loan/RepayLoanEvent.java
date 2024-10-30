package com.nerdysoft.axon.event.loan;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RepayLoanEvent {
    private UUID accountId;
}
