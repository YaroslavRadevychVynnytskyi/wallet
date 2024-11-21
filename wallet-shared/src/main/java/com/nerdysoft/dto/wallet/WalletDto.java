package com.nerdysoft.dto.wallet;

import com.nerdysoft.model.enums.Currency;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WalletDto {
  private UUID walletId;
  private UUID accountId;
  private BigDecimal balance;
  private Currency currency;
  private LocalDateTime createdAt;
}
