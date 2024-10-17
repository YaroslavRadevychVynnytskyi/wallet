package com.nerdysoft.dto.feign;

import com.nerdysoft.model.enums.Currency;
import java.math.BigDecimal;
import java.util.UUID;

public record TransferRequestDto(
        UUID toWalletId,
        BigDecimal amount,
        Currency currency
) {
}
