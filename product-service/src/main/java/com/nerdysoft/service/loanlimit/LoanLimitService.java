package com.nerdysoft.service.loanlimit;

import com.nerdysoft.entity.loanlimit.LoanLimit;
import java.math.BigDecimal;
import java.util.UUID;

public interface LoanLimitService {
    LoanLimit findById(UUID id);

    LoanLimit getLoanLimit(UUID accountID, String email);

    LoanLimit getLoanLimitByAccountId(UUID walletId);

    LoanLimit subtractAvailableLoanLimitAmount(UUID loanLimitId, BigDecimal usedLoanLimitAmount);

    LoanLimit repayLoanLimit(UUID accountId);

    LoanLimit cancelUpdateLoanLimit(UUID loanLimitId, BigDecimal usedAvailableAmount);
}
