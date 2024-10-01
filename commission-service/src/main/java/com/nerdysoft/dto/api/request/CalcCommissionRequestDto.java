package com.nerdysoft.dto.api.request;

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
