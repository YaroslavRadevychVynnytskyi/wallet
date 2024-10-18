package com.nerdysoft.service;

import com.nerdysoft.dto.request.TransferTransactionRequestDto;
import com.nerdysoft.repository.TransactionRepository;
import com.nerdysoft.entity.Transaction;
import com.nerdysoft.entity.enums.TransactionStatus;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TransactionService {
  private final TransactionRepository transactionRepository;

  public Transaction saveTransaction(UUID walletId, TransferTransactionRequestDto requestDto,
      TransactionStatus status, BigDecimal walletBalance) {
    Transaction transaction = Transaction.builder()
        .walletId(walletId)
        .amount(requestDto.getAmount())
        .walletBalance(walletBalance)
        .currency(requestDto.getCurrency())
        .status(status)
        .toWalletId(requestDto.getToWalletId())
        .build();
    return transactionRepository.save(transaction);
  }

  public List<Transaction> getAllByWalletId(UUID walletId) {
    return transactionRepository.findAllByWalletId(walletId);
  }
}
