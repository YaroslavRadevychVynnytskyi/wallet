package com.nerdysoft.axon.event.loan;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RepayLoanEvent {
    private UUID accountId;
}
