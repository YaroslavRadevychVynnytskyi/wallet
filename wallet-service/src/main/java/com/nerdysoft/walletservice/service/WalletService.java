package com.nerdysoft.walletservice.service;

import com.nerdysoft.walletservice.dto.request.CreateWalletDto;
import com.nerdysoft.walletservice.exception.AccountHasAlreadyWalletOnThisCurrency;
import com.nerdysoft.walletservice.model.Wallet;
import com.nerdysoft.walletservice.model.enums.Currency;
import com.nerdysoft.walletservice.repository.WalletRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WalletService {
  private final WalletRepository walletRepository;

  public Wallet createWallet(CreateWalletDto createWalletDto) {
    if (walletRepository.hasAccountWalletOnThisCurrency(createWalletDto.accountId(), createWalletDto.currency())) {
      throw new AccountHasAlreadyWalletOnThisCurrency();
    } else {
      Wallet wallet = new Wallet(createWalletDto);
      return walletRepository.save(wallet);
    }
  }

  public Wallet getWallet(UUID walletId) {
    return walletRepository.findById(walletId).orElseThrow(EntityNotFoundException::new);
  }

  public Wallet updateCurrency(UUID walletId, Currency currency) {
    Wallet wallet = getWallet(walletId);
    wallet.setCurrency(currency);
    return walletRepository.save(wallet);
  }

  public String deleteWallet(UUID walletId) {
    Wallet wallet = getWallet(walletId);
    walletRepository.deleteById(walletId);
    return String.format("Wallet with id %s was deleted", walletId);
  }
}
