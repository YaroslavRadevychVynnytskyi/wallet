package com.nerdysoft.dto.api.response;

import com.nerdysoft.model.enums.OperationType;
import com.nerdysoft.model.enums.ReserveType;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateBalanceResponseDto {
  private UUID id;
  private ReserveType reserveType;
  private BigDecimal balance;
  private BigDecimal amount;
  private OperationType operationType;
}
