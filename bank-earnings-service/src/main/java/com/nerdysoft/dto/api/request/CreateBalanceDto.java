package com.nerdysoft.dto.api.request;

import com.nerdysoft.entity.reserve.enums.ReserveType;
import java.math.BigDecimal;
import lombok.Data;

@Data
public class CreateBalanceDto {
    private ReserveType reserveType;
    private BigDecimal amount;
}
