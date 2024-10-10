package com.nerdysoft.dto.api.request;

import com.nerdysoft.dto.api.request.enums.OperationType;
import com.nerdysoft.dto.feign.Currency;
import java.math.BigDecimal;

public record UpdateBalanceRequestDto(
        BigDecimal amount,
        Currency currency,
        OperationType operationType
) {
}
