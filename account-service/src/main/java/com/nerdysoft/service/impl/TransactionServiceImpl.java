package com.nerdysoft.service.impl;

import com.nerdysoft.dto.api.request.CreateTransactionRequestDto;
import com.nerdysoft.dto.api.request.GenericTransactionRequestDto;
import com.nerdysoft.dto.api.response.GenericTransactionResponseDto;
import com.nerdysoft.dto.api.response.TransactionResponseDto;
import com.nerdysoft.dto.event.activity.enums.ActionType;
import com.nerdysoft.dto.event.activity.enums.EntityType;
import com.nerdysoft.dto.feign.Transaction;
import com.nerdysoft.dto.feign.TransferRequestDto;
import com.nerdysoft.dto.feign.Wallet;
import com.nerdysoft.dto.feign.WalletTransactionRequestDto;
import com.nerdysoft.dto.feign.WalletTransactionResponseDto;
import com.nerdysoft.feign.WalletFeignClient;
import com.nerdysoft.mapper.TransactionMapper;
import com.nerdysoft.model.enums.Currency;
import com.nerdysoft.service.EventProducer;
import com.nerdysoft.service.TransactionService;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {
  private final WalletFeignClient walletFeignClient;
  private final TransactionMapper transactionMapper;
  private final EventProducer eventProducer;

  @Override
  public TransactionResponseDto createTransaction(UUID accountId,
      CreateTransactionRequestDto dto,
      Currency fromWalletCurrency,
      Currency toWalletCurrency) {
    Wallet fromWallet = walletFeignClient.getWalletByAccountIdAndCurrency(
            accountId,
            fromWalletCurrency)
        .getBody();

    Wallet toWallet = walletFeignClient.getWalletByAccountIdAndCurrency(
            dto.toAccountId(),
            toWalletCurrency)
        .getBody();

    TransferRequestDto transferRequestDto = new TransferRequestDto(
        toWallet.getWalletId(),
        dto.amount(),
        dto.currency()
    );

    Transaction transaction = walletFeignClient.transfer(
            fromWallet.getWalletId(),
            transferRequestDto)
        .getBody();

    eventProducer.sendEvent(transactionMapper.toTransactionEvent(transaction));
    eventProducer.sendEvent(
        accountId,
        transaction.transactionId(),
        ActionType.CREATE,
        EntityType.TRANSACTION,
        Optional.empty(),
        Optional.of(transaction));

    return new TransactionResponseDto(transaction, accountId, dto.toAccountId());
  }

  @Override
  public GenericTransactionResponseDto updateBalance(UUID accountId, GenericTransactionRequestDto dto) {
    Wallet wallet = walletFeignClient.getWalletByAccountIdAndCurrency(accountId, dto.walletCurrency()).getBody();

    WalletTransactionResponseDto walletTransactionResponseDto = processUpdateBalanceTransaction(wallet, dto);

    eventProducer.sendEvent(transactionMapper.toTransactionEvent(walletTransactionResponseDto, dto.transactionType()));
    eventProducer.sendEvent(
        accountId,
        walletTransactionResponseDto.transactionId(),
        ActionType.CREATE,
        EntityType.TRANSACTION,
        Optional.empty(),
        Optional.of(transactionMapper.toTransactionEvent(walletTransactionResponseDto, dto.transactionType()))
    );

    return new GenericTransactionResponseDto(walletTransactionResponseDto, accountId);
  }

  private WalletTransactionResponseDto processUpdateBalanceTransaction(Wallet wallet, GenericTransactionRequestDto dto) {
    WalletTransactionRequestDto transactionRequestDto = new WalletTransactionRequestDto(dto.amount(), dto.currency());

    switch (dto.transactionType()) {
      case ACCOUNT_DEPOSIT -> {
        return walletFeignClient.deposit(wallet.getWalletId(), transactionRequestDto).getBody();
      }
      case ACCOUNT_WITHDRAW -> {
        return walletFeignClient.withdraw(wallet.getWalletId(), transactionRequestDto).getBody();
      }
      default -> throw new IllegalArgumentException(String.format("Unsupported operation type %s", dto.transactionType()));
    }
  }
}
