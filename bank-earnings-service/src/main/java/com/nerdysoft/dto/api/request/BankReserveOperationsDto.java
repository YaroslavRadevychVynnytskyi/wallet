package com.nerdysoft.dto.api.request;

import com.nerdysoft.entity.reserve.enums.ReserveType;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BankReserveOperationsDto {
    private ReserveType reserveType;
    private BigDecimal amount;
}
