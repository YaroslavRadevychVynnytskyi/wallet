package com.nerdysoft.dto.feign;

import com.nerdysoft.dto.feign.enums.Currency;
import java.math.BigDecimal;

public record TransactionRequestDto(
        BigDecimal amount,
        Currency currency
) {
}
