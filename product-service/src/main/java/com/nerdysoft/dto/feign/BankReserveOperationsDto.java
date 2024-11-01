package com.nerdysoft.dto.feign;

import com.nerdysoft.model.enums.ReserveType;
import java.math.BigDecimal;

public record BankReserveOperationsDto(
        ReserveType reserveType,
        BigDecimal amount
) {
}
