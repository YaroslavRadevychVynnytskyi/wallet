package com.nerdysoft.axon.handler.query.wallet;

import com.nerdysoft.axon.query.wallet.FindWalletByAccountIdAndCurrencyQuery;
import com.nerdysoft.axon.query.wallet.FindWalletByIdQuery;
import com.nerdysoft.dto.wallet.WalletDto;
import com.nerdysoft.entity.Wallet;
import com.nerdysoft.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.beans.BeanUtils;
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
  public WalletDto handle(FindWalletByAccountIdAndCurrencyQuery query) {
    Wallet wallet = walletService.findWalletByAccountIdAndCurrency(query.getAccountId(), query.getCurrency());
    WalletDto dto = new WalletDto();

    BeanUtils.copyProperties(wallet, dto);

    return dto;
  }
}
