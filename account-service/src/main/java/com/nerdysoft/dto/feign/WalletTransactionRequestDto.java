package com.nerdysoft.dto.feign;

import com.nerdysoft.model.enums.Currency;
import java.math.BigDecimal;

public record WalletTransactionRequestDto(
        BigDecimal amount,
        Currency currency
) {
}
