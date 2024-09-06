package com.nerdysoft.walletservice.service;

import com.nerdysoft.walletservice.dto.request.TransactionRequestDto;
import com.nerdysoft.walletservice.dto.request.TransferRequestDto;
import com.nerdysoft.walletservice.model.Transaction;
import com.nerdysoft.walletservice.model.enums.TransactionStatus;
import com.nerdysoft.walletservice.repository.TransactionRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TransactionService {

  private final TransactionRepository transactionRepository;

  public Transaction saveTransaction(UUID walletId, TransactionRequestDto transactionRequestDto,
      TransactionStatus status) {
    Transaction transaction = new Transaction(walletId, transactionRequestDto.amount(),
        transactionRequestDto.currency(), status);
    return transactionRepository.save(transaction);
  }

  public Transaction saveTransaction(UUID walletId, TransferRequestDto transferRequestDto,
      TransactionStatus status) {
    Transaction transaction = new Transaction(walletId, transferRequestDto.amount(),
        transferRequestDto.currency(), status, transferRequestDto.toWalletId());
    return transactionRepository.save(transaction);
  }
}
