package com.nerdysoft.dto.api.request;

import com.nerdysoft.model.enums.ReserveType;
import java.math.BigDecimal;
import lombok.Data;

@Data
public class CreateBalanceDto {
    private ReserveType reserveType;
    private BigDecimal amount;
}
