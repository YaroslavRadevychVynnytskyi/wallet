package com.nerdysoft.axon.query.transaction;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class FindAllTransactionsByWalletIdQuery {
  private UUID walletId;
}
