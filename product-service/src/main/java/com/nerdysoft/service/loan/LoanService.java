package com.nerdysoft.service.loan;

import com.nerdysoft.dto.api.request.loan.LoanRequestDto;
import com.nerdysoft.entity.loan.Loan;
import com.nerdysoft.entity.loan.LoanPayment;
import java.util.UUID;

public interface LoanService {
    Loan applyForLoan(UUID accountId, String email, LoanRequestDto requestDto);

    LoanPayment manualLoanRepay(UUID accountId);

    void autoLoanRepay();
}
