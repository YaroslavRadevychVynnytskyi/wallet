package com.nerdysoft.service.loanlimit.impl;

import com.nerdysoft.entity.loanlimit.LoanLimit;
import com.nerdysoft.repo.loanlimit.LoanLimitRepository;
import com.nerdysoft.service.analyzer.AccountBalanceAnalyzer;
import com.nerdysoft.service.loanlimit.LoanLimitService;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoanLimitServiceImpl implements LoanLimitService {
    private final LoanLimitRepository loanLimitRepository;
    private final AccountBalanceAnalyzer accountBalanceAnalyzer;
    //private final LoanLimitStrategy loanLimitStrategy;


    @Override
    public LoanLimit getLoanLimit(UUID accountId) {

        LoanLimit loanLimit;

        /*
        if () {
            loanLimit = new LoanLimit(accountId, BigDecimal.valueOf(100));
        } else if () {
            loanLimit = new LoanLimit(accountId, BigDecimal.valueOf(200));
        } else if () {
            loanLimit = new LoanLimit(accountId, BigDecimal.valueOf(500));
        }

         */


        return null;
    }
}
