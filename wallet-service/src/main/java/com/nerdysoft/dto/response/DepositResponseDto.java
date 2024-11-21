package com.nerdysoft.dto.response;

import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor
public class DepositResponseDto extends WalletOperationResponseDto {
  private UUID walletId;
}
