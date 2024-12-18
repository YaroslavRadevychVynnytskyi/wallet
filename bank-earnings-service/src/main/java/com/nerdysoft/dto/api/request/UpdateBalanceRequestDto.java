package com.nerdysoft.dto.api.request;

import com.nerdysoft.model.enums.OperationType;
import com.nerdysoft.model.enums.ReserveType;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateBalanceRequestDto {
    private UUID id;
    private ReserveType reserveType;
    private BigDecimal amount;
    private OperationType operationType;
}
