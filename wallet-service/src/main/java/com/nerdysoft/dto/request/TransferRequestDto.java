package com.nerdysoft.dto.request;

import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor
public class TransferRequestDto extends WalletOperationRequestDto {
  private UUID toWalletId;
}
