package com.nerdysoft.dto.feign;

import com.nerdysoft.dto.feign.enums.OperationType;
import com.nerdysoft.dto.feign.enums.ReserveType;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UpdateBalanceDto {
    private UUID id;
    private ReserveType reserveType;
    private BigDecimal amount;
    private OperationType operationType;
}
