package com.nerdysoft.walletservice.service;

import com.nerdysoft.walletservice.dto.request.TransactionRequestDto;
import com.nerdysoft.walletservice.dto.request.TransferRequestDto;
import com.nerdysoft.walletservice.model.Transaction;
import com.nerdysoft.walletservice.model.enums.TransactionStatus;
import com.nerdysoft.walletservice.repository.TransactionRepository;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TransactionService {
  private final TransactionRepository transactionRepository;

  public Transaction saveTransaction(UUID walletId, TransactionRequestDto transactionRequestDto,
      TransactionStatus status, BigDecimal walletBalance) {
    Transaction transaction = Transaction.builder()
        .walletId(walletId)
        .amount(transactionRequestDto.amount())
        .walletBalance(walletBalance)
        .currency(transactionRequestDto.currency())
        .status(status)
        .build();
    return transactionRepository.save(transaction);
  }

  public Transaction saveTransaction(UUID walletId, TransferRequestDto transferRequestDto,
      TransactionStatus status, BigDecimal walletBalance) {
    Transaction transaction = Transaction.builder()
        .walletId(walletId)
        .amount(transferRequestDto.amount())
        .walletBalance(walletBalance)
        .currency(transferRequestDto.currency())
        .status(status)
        .toWalletId(transferRequestDto.toWalletId())
        .build();
    return transactionRepository.save(transaction);
  }
}
