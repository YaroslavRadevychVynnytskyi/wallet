package com.nerdysoft.service.loanlimit;

import com.nerdysoft.entity.loanlimit.LoanLimit;
import java.util.UUID;

public interface LoanLimitService {
    LoanLimit getLoanLimit(UUID accountId);
}
