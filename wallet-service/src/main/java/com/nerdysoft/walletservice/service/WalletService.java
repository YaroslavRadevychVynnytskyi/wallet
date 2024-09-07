package com.nerdysoft.walletservice.service;

import com.nerdysoft.walletservice.dto.request.CreateWalletDto;
import com.nerdysoft.walletservice.dto.request.TransactionRequestDto;
import com.nerdysoft.walletservice.dto.request.TransferRequestDto;
import com.nerdysoft.walletservice.exception.AccountHasAlreadyWalletOnThisCurrencyException;
import com.nerdysoft.walletservice.model.Transaction;
import com.nerdysoft.walletservice.model.Wallet;
import com.nerdysoft.walletservice.model.enums.Currency;
import com.nerdysoft.walletservice.model.enums.TransactionStatus;
import com.nerdysoft.walletservice.repository.WalletRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiFunction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WalletService {
  private final WalletRepository walletRepository;

  private final TransactionService transactionService;

  public Wallet createWallet(CreateWalletDto createWalletDto) {
    if (walletRepository.hasAccountWalletOnThisCurrency(createWalletDto.accountId(),
        createWalletDto.currency())) {
      throw new AccountHasAlreadyWalletOnThisCurrencyException();
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
    getWallet(walletId);
    walletRepository.deleteById(walletId);
    return String.format("Wallet with id %s was deleted", walletId);
  }

  public Transaction transaction(UUID walletId, TransactionRequestDto transactionRequestDto,
      BiFunction<Double, Double, Double> operation) {
    Optional<Wallet> wallet = walletRepository.findById(walletId);
    if (wallet.isPresent() && wallet.get().getCurrency().equals(transactionRequestDto.currency())) {
      Double balance = operation.apply(wallet.get().getBalance(), transactionRequestDto.amount());
      if (balance >= 0) {
        wallet.get().setBalance(balance);
        walletRepository.save(wallet.get());
        return transactionService.saveTransaction(walletId, transactionRequestDto,
            TransactionStatus.SUCCESS);
      } else {
        return transactionService.saveTransaction(walletId, transactionRequestDto,
            TransactionStatus.FAILURE);
      }
    } else {
      return transactionService.saveTransaction(walletId, transactionRequestDto,
          TransactionStatus.FAILURE);
    }
  }

  public Transaction transferToAnotherWallet(UUID walletId, TransferRequestDto transferRequestDto) {
    Optional<Wallet> senderWallet = walletRepository.findById(walletId);
    Optional<Wallet> receivingWallet = walletRepository.findById(transferRequestDto.toWalletId());
    if (
        (senderWallet.isPresent() && receivingWallet.isPresent()) &&
        (senderWallet.get().getCurrency().equals(receivingWallet.get().getCurrency()))
    ) {
      double senderWalletBalance = senderWallet.get().getBalance() - transferRequestDto.amount();
      if (senderWalletBalance >= 0) {
        senderWallet.get()
            .setBalance(senderWalletBalance);
        receivingWallet.get()
            .setBalance(receivingWallet.get().getBalance() + transferRequestDto.amount());
        walletRepository.saveAll(List.of(senderWallet.get(), receivingWallet.get()));
        return transactionService.saveTransaction(walletId, transferRequestDto,
            TransactionStatus.SUCCESS);
      } else {
        return transactionService.saveTransaction(walletId, transferRequestDto,
            TransactionStatus.FAILURE);
      }
    } else {
      return transactionService.saveTransaction(walletId, transferRequestDto,
          TransactionStatus.FAILURE);
    }
  }

  public Wallet getWalletByAccountIdAndCurrency(UUID accountId, Currency currency) {
    return walletRepository.findByAccountIdAndCurrency(accountId, currency)
        .orElseThrow(EntityNotFoundException::new);
  }
}
