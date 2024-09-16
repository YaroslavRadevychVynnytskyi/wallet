package com.nerdysoft.dto.api.request;

import com.nerdysoft.dto.feign.Currency;
import java.math.BigDecimal;
import java.util.UUID;

public record CreateTransactionRequestDto(
        UUID toAccountId,
        BigDecimal amount,
        Currency currency
) {
}