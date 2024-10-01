package com.nerdysoft.dto.feign;

import java.math.BigDecimal;
import java.util.UUID;

public record CalcCommissionRequestDto(
        UUID transactionId,
        BigDecimal amount,
        String fromWalletCurrency,
        String toWalletCurrency,
        String transactionCurrency
) {
}
