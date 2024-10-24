package com.nerdysoft.walletservice.axon.handler.query;

import com.nerdysoft.walletservice.axon.query.wallet.FindWalletByAccountIdAndCurrencyQuery;
import com.nerdysoft.walletservice.axon.query.wallet.FindWalletByIdQuery;
import com.nerdysoft.walletservice.model.Wallet;
import com.nerdysoft.walletservice.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WalletQueryHandler {
  private final WalletService walletService;

  @QueryHandler
  public Wallet handle(FindWalletByIdQuery query) {
    return walletService.findById(query.getId());
  }

  @QueryHandler
  public Wallet handle(FindWalletByAccountIdAndCurrencyQuery query) {
    return walletService.findWalletByAccountIdAndCurrency(query.getAccountId(), query.getCurrency());
  }
}
