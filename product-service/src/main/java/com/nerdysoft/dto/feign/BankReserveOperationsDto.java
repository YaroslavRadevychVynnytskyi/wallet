package com.nerdysoft.dto.feign;

import com.nerdysoft.dto.feign.enums.ReserveType;
import java.math.BigDecimal;

public record BankReserveOperationsDto(
        ReserveType reserveType,
        BigDecimal amount
) {
}
