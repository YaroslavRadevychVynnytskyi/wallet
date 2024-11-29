package com.nerdysoft.axon.query.loanlimit;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class FindLoanLimitByAccountIdQuery {
    private UUID accountId;
}
