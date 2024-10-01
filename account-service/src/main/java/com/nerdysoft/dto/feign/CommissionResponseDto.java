package com.nerdysoft.dto.feign;

import java.math.BigDecimal;
import java.util.UUID;

public record CommissionResponseDto(
        UUID transactionId,
        BigDecimal commissionAmount
) {
}
