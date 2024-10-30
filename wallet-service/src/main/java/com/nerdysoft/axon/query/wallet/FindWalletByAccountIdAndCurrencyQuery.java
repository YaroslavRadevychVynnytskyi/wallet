package com.nerdysoft.axon.query.wallet;

import com.nerdysoft.model.enums.Currency;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class FindWalletByAccountIdAndCurrencyQuery {
  private UUID accountId;

  private Currency currency;
}
