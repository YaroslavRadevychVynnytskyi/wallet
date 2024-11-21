package com.nerdysoft.dto.response;

import java.math.BigDecimal;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor
public class WithdrawResponseDto extends WalletOperationResponseDto {
  private UUID walletId;

  private boolean usedLoanLimit;

  private BigDecimal usedLoanLimitAmount;
}
