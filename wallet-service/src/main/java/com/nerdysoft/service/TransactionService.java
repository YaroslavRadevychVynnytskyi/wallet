package com.nerdysoft.service;

import com.nerdysoft.axon.command.transaction.CreateTransactionCommand;
import com.nerdysoft.entity.Transaction;
import com.nerdysoft.model.enums.TransactionStatus;
import com.nerdysoft.repository.TransactionRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TransactionService {
  private final TransactionRepository transactionRepository;

  public Transaction findById(UUID id) {
    return transactionRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Transaction not found"));
  }

  public Transaction saveTransaction(CreateTransactionCommand command) {
    Transaction.TransactionBuilder builder = Transaction.builder()
        .walletId(command.getWalletId())
        .walletBalance(command.getWalletBalance())
        .amount(command.getAmount())
        .usedLoanLimit(command.isUsedLoanLimit())
        .operationCurrency(command.getOperationCurrency())
        .walletCurrency(command.getWalletCurrency())
        .status(command.getStatus());

    if (command.isUsedLoanLimit()) {
      builder.usedLoanLimitAmount(command.getUsedLoanLimitAmount());
    }

    if (command.getToWalletId() != null) {
      builder.toWalletId(command.getToWalletId());
    }

    return transactionRepository.save(builder.build());
  }

  public List<Transaction> findAllByWalletId(UUID walletId) {
    return transactionRepository.findAllByWalletId(walletId);
  }

  public Transaction updateTransactionStatus(UUID transactionId, TransactionStatus status) {
    Transaction transaction = findById(transactionId);

    transaction.setStatus(status);

    transactionRepository.save(transaction);

    return transaction;
  }
}
