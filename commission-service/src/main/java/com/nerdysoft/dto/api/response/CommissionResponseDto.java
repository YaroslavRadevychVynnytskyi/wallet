package com.nerdysoft.dto.api.response;

import java.math.BigDecimal;
import java.util.UUID;

public record CommissionResponseDto(
        UUID transactionId,
        BigDecimal commissionAmount
) {
}
