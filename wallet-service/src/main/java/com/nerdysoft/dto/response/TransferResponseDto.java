package com.nerdysoft.dto.response;

import java.math.BigDecimal;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor
public class TransferResponseDto extends WalletOperationResponseDto {
  private UUID fromWalletId;

  private UUID toWalletId;

  private boolean isUsedLoanLimit;

  private BigDecimal usedLoanLimitAmount;
}
