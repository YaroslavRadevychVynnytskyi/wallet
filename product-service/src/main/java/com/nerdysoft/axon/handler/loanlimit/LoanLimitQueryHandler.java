package com.nerdysoft.axon.handler.loanlimit;

import com.nerdysoft.axon.query.FindLoanLimitByIdQuery;
import com.nerdysoft.axon.query.FindLoanLimitByWalletIdQuery;
import com.nerdysoft.entity.loanlimit.LoanLimit;
import com.nerdysoft.repo.loanlimit.LoanLimitRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LoanLimitQueryHandler {
    private final LoanLimitRepository loanLimitRepository;

    @QueryHandler
    public LoanLimit findLoanLimitById(FindLoanLimitByIdQuery query) {
        return loanLimitRepository.findById(query.getId()).orElseThrow(() ->
                new EntityNotFoundException("Can't find loan limit with ID: " + query.getId()));
    }

    @QueryHandler
    public LoanLimit findLoanLimitByWalletId(FindLoanLimitByWalletIdQuery query) {
        return loanLimitRepository.findByWalletIdAndIsRepaidFalse(query.getWalletId()).orElseThrow(() ->
                new EntityNotFoundException("Can't find loan limit with wallet ID: " + query.getWalletId()));
    }
}
