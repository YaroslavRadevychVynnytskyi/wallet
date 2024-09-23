package com.nerdysoft.walletservice.service;

import com.nerdysoft.walletservice.dto.request.ConvertAmountRequestDto;
import com.nerdysoft.walletservice.dto.request.CreateWalletDto;
import com.nerdysoft.walletservice.dto.request.TransactionRequestDto;
import com.nerdysoft.walletservice.dto.request.TransferRequestDto;
import com.nerdysoft.walletservice.dto.response.ConvertAmountResponseDto;
import com.nerdysoft.walletservice.dto.response.TransactionResponseDto;
import com.nerdysoft.walletservice.dto.response.TransferResponseDto;
import com.nerdysoft.walletservice.exception.UniqueException;
import com.nerdysoft.walletservice.feign.CurrencyExchangeFeignClient;
import com.nerdysoft.walletservice.mapper.TransactionMapper;
import com.nerdysoft.walletservice.model.Wallet;
import com.nerdysoft.walletservice.model.enums.Currency;
import com.nerdysoft.walletservice.model.enums.TransactionStatus;
import com.nerdysoft.walletservice.repository.WalletRepository;
import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiFunction;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WalletService {
  private final WalletRepository walletRepository;

  private final TransactionService transactionService;

  private final TransactionMapper transactionMapper;

  private final CurrencyExchangeFeignClient currencyExchangeFeignClient;

  public Wallet createWallet(CreateWalletDto createWalletDto) {
    if (walletRepository.hasAccountWalletOnThisCurrency(createWalletDto.accountId(),
        createWalletDto.currency())) {
      throw new UniqueException(String.format("This account has already wallet on %s currency",
          createWalletDto.currency()), HttpStatus.NOT_ACCEPTABLE);
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

  @Transactional
  public TransactionResponseDto transaction(UUID walletId,
      TransactionRequestDto transactionRequestDto,
      BiFunction<BigDecimal, BigDecimal, BigDecimal> operation) {
    Optional<Wallet> wallet = walletRepository.findById(walletId);
    if (wallet.isPresent()) {
      BigDecimal balance;
      if (wallet.get().getCurrency().equals(transactionRequestDto.currency())) {
        balance = operation.apply(wallet.get().getBalance(), transactionRequestDto.amount());
      } else {
        ConvertAmountResponseDto convertAmountResponseDto = currencyExchangeFeignClient.convert(
                new ConvertAmountRequestDto(transactionRequestDto.currency().getCode(),
                    wallet.get().getCurrency().getCode(), transactionRequestDto.amount()))
            .getBody();
        balance = operation.apply(wallet.get().getBalance(), convertAmountResponseDto.convertedAmount());
      }
      if (balance.compareTo(BigDecimal.ZERO) >= 0) {
        wallet.get().setBalance(balance);
        walletRepository.save(wallet.get());
        return transactionMapper.transactionToTransactionResponseDto(
            transactionService.saveTransaction(walletId, transactionRequestDto,
                TransactionStatus.SUCCESS));
      } else {
        return transactionMapper.transactionToTransactionResponseDto(
            transactionService.saveTransaction(walletId, transactionRequestDto,
                TransactionStatus.FAILURE));
      }
    } else {
      return transactionMapper.transactionToTransactionResponseDto(
          transactionService.saveTransaction(walletId, transactionRequestDto,
              TransactionStatus.FAILURE));
    }
  }

  @Transactional
  public TransferResponseDto transferToAnotherWallet(UUID walletId,
      TransferRequestDto transferRequestDto) {
    Optional<Wallet> senderWallet = walletRepository.findById(walletId);
    Optional<Wallet> receivingWallet = walletRepository.findById(transferRequestDto.toWalletId());
    if (senderWallet.isPresent() && receivingWallet.isPresent()) {
      BigDecimal senderWalletBalance;
      if (senderWallet.get().getCurrency().equals(transferRequestDto.currency())) {
        senderWalletBalance = senderWallet.get().getBalance()
            .subtract(transferRequestDto.amount());
      } else {
        ConvertAmountResponseDto senderWalletConversion = currencyExchangeFeignClient.convert(
                new ConvertAmountRequestDto(transferRequestDto.currency().getCode(),
                    senderWallet.get().getCurrency().getCode(), transferRequestDto.amount()))
            .getBody();
        senderWalletBalance = senderWallet.get().getBalance()
            .subtract(senderWalletConversion.convertedAmount());
      }
      if (senderWalletBalance.compareTo(BigDecimal.ZERO) >= 0) {
        BigDecimal amountToSent = senderWallet.get().getBalance().subtract(senderWalletBalance);
        senderWallet.get().setBalance(senderWalletBalance);
        if (senderWallet.get().getCurrency().equals(receivingWallet.get().getCurrency())) {
          receivingWallet.get()
              .setBalance(receivingWallet.get().getBalance().add(amountToSent));
        } else {
          ConvertAmountResponseDto receivingWalletConversion = currencyExchangeFeignClient.convert(
                  new ConvertAmountRequestDto(senderWallet.get().getCurrency().getCode(),
                      receivingWallet.get().getCurrency().getCode(), amountToSent)).getBody();
          receivingWallet.get().setBalance(receivingWallet.get().getBalance()
                  .add(receivingWalletConversion.convertedAmount()));
        }
        walletRepository.saveAll(List.of(senderWallet.get(), receivingWallet.get()));
        return transactionMapper.transactionToTransferResponseDto(
            transactionService.saveTransaction(walletId, transferRequestDto,
                TransactionStatus.SUCCESS));
      } else {
        return transactionMapper.transactionToTransferResponseDto(
            transactionService.saveTransaction(walletId, transferRequestDto,
                TransactionStatus.FAILURE));
      }
    } else {
      return transactionMapper.transactionToTransferResponseDto(
          transactionService.saveTransaction(walletId, transferRequestDto,
              TransactionStatus.FAILURE));
    }
  }

  public Wallet getWalletByAccountIdAndCurrency(UUID accountId, Currency currency) {
    return walletRepository.findByAccountIdAndCurrency(accountId, currency)
        .orElseThrow(EntityNotFoundException::new);
  }
}
