package com.nerdysoft.service.loanlimit;

import com.nerdysoft.dto.api.request.LoanLimitRequestDto;
import com.nerdysoft.entity.loanlimit.LoanLimit;
import java.util.UUID;

public interface LoanLimitService {
    LoanLimit getLoanLimit(LoanLimitRequestDto requestDto);

    LoanLimit getLoanLimitByWalletId(UUID walletId);

    LoanLimit updateByWalletId(UUID walletId, LoanLimit loanLimit);
}
