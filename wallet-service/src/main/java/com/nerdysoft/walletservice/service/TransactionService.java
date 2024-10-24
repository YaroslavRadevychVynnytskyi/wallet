package com.nerdysoft.walletservice.service;

import com.nerdysoft.model.enums.TransactionStatus;
import com.nerdysoft.walletservice.dto.request.WalletOperationRequestDto;
import com.nerdysoft.walletservice.model.Transaction;
import com.nerdysoft.walletservice.repository.TransactionRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TransactionService {
  private final TransactionRepository transactionRepository;

  public Transaction saveTransaction(UUID walletId, WalletOperationRequestDto requestDto,
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

  public List<Transaction> findAllByWalletId(UUID walletId) {
    return transactionRepository.findAllByWalletId(walletId);
  }
}
