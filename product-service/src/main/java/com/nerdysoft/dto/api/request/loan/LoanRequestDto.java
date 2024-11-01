package com.nerdysoft.dto.api.request.loan;

import com.nerdysoft.model.enums.Currency;
import com.nerdysoft.model.enums.PaymentType;
import java.math.BigDecimal;

public record LoanRequestDto(
        BigDecimal requestedAmount,
        Currency currency,
        PaymentType paymentType
) {
}
