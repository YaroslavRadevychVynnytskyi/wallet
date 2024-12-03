package com.nerdysoft.axon.handler.loanlimit;

import com.nerdysoft.axon.query.FindLoanLimitByIdQuery;
import com.nerdysoft.axon.query.loanlimit.FindLoanLimitByAccountIdQuery;
import com.nerdysoft.dto.loanlimit.LoanLimitDto;
import com.nerdysoft.entity.loanlimit.LoanLimit;
import com.nerdysoft.service.loanlimit.LoanLimitService;
import lombok.RequiredArgsConstructor;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LoanLimitQueryHandler {
    private final LoanLimitService loanLimitService;

    @QueryHandler
    public LoanLimit findLoanLimitById(FindLoanLimitByIdQuery query) {
        return loanLimitService.findById(query.getId());
    }

    @QueryHandler
    public LoanLimitDto findLoanLimitByWalletId(FindLoanLimitByAccountIdQuery query) {
        LoanLimit loanLimit = loanLimitService.getLoanLimitByAccountId(query.getAccountId());
        LoanLimitDto dto = new LoanLimitDto();

        BeanUtils.copyProperties(loanLimit, dto);

        return dto;
    }
}
