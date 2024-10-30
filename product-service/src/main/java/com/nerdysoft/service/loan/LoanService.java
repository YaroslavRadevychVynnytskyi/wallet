package com.nerdysoft.service.loan;

import com.nerdysoft.dto.feign.enums.Currency;
import com.nerdysoft.entity.loan.Loan;
import com.nerdysoft.entity.loan.LoanPayment;
import com.nerdysoft.model.enums.PaymentType;
import java.math.BigDecimal;
import java.util.UUID;

public interface LoanService {
    Loan applyForLoan(UUID accountId, String email, BigDecimal requestedAmount, Currency currency, PaymentType paymentType);

    LoanPayment manualLoanRepay(UUID accountId);

    void autoLoanRepay();
}
