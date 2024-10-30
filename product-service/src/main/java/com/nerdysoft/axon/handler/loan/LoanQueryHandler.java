package com.nerdysoft.axon.handler.loan;

import com.nerdysoft.axon.query.FindLoanByIdQuery;
import com.nerdysoft.entity.loan.Loan;
import com.nerdysoft.repo.loan.LoanRepository;
import lombok.RequiredArgsConstructor;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LoanQueryHandler {
    private final LoanRepository loanRepository;

    @QueryHandler
    public Loan findLoanById(FindLoanByIdQuery query) {
        return loanRepository.findById(query.getLoanId()).orElseThrow();
    }
}
