package com.nerdysoft.dto.feign;

import com.nerdysoft.dto.feign.enums.Currency;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record Wallet(
        UUID walletId,
        UUID accountId,
        BigDecimal balance,
        Currency currency,
        LocalDateTime createdAt
) {
}
