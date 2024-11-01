package com.nerdysoft.service.loanlimit;

import com.nerdysoft.entity.loanlimit.LoanLimit;
import com.nerdysoft.model.enums.Currency;
import java.util.UUID;

public interface LoanLimitService {
    LoanLimit getLoanLimit(UUID accountID, String accountEmail, Currency currency);

    LoanLimit getLoanLimitByWalletId(UUID walletId);

    LoanLimit updateByWalletId(UUID walletId, LoanLimit loanLimit);

    LoanLimit repayLoanLimit(UUID accountId);
}
