package com.nerdysoft.axon.query;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FindLoanByIdQuery {
    private UUID loanId;
}
