package com.nerdysoft.dto.feign;

import java.math.BigDecimal;

public record WalletTransactionRequestDto(
        BigDecimal amount,
        Currency currency
) {
}
