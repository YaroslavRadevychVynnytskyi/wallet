package com.nerdysoft.service.loanlimit;

import com.nerdysoft.axon.command.loanlimit.CancelSubtractionFromLoanLimitCommand;
import com.nerdysoft.entity.loanlimit.LoanLimit;
import com.nerdysoft.model.enums.Currency;
import java.math.BigDecimal;
import java.util.UUID;

public interface LoanLimitService {
    LoanLimit findById(UUID id);

    LoanLimit getLoanLimit(UUID accountID, String accountEmail, Currency currency);

    LoanLimit getLoanLimitByWalletId(UUID walletId);

    LoanLimit subtractAvailableLoanLimitAmount(UUID loanLimitId, BigDecimal usedLoanLimitAmount);

    LoanLimit repayLoanLimit(UUID accountId);

    LoanLimit cancelSubtractionFromLoanLimit(CancelSubtractionFromLoanLimitCommand command);
}
