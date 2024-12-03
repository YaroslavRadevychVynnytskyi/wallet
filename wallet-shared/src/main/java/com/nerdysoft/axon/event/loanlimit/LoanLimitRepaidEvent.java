package com.nerdysoft.axon.event.loanlimit;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LoanLimitRepaidEvent {
    private UUID loanLimitId;

    private UUID accountId;
}
