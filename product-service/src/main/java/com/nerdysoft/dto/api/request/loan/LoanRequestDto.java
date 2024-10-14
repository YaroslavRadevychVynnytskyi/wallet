package com.nerdysoft.dto.api.request.loan;

import com.nerdysoft.dto.api.request.loan.enums.PaymentType;
import com.nerdysoft.dto.feign.enums.Currency;
import java.math.BigDecimal;

public record LoanRequestDto(
        BigDecimal requestedAmount,
        Currency currency,
        PaymentType paymentType
) {
}
