package com.nerdysoft.dto.feign;

import java.math.BigDecimal;
import java.util.UUID;

public record TransferRequestDto(
        UUID toWalletId,
        BigDecimal amount,
        Currency currency
) {
}
