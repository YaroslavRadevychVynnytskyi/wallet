package com.nerdysoft.mapper;

import com.nerdysoft.config.MapperConfig;
import com.nerdysoft.dto.response.DepositResponseDto;
import com.nerdysoft.dto.response.TransferResponseDto;
import com.nerdysoft.dto.response.WithdrawResponseDto;
import com.nerdysoft.entity.Transaction;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface TransactionMapper {
  DepositResponseDto toDepositResponseDto(Transaction transaction);

  WithdrawResponseDto toWithdrawResponseDto(Transaction transaction);

  default TransferResponseDto toTransferResponseDto(Transaction transaction) {
    return TransferResponseDto.builder()
        .transactionId(transaction.getTransactionId())
        .accountId(transaction.getAccountId())
        .amount(transaction.getAmount())
        .walletBalance(transaction.getWalletBalance())
        .operationCurrency(transaction.getOperationCurrency())
        .walletCurrency(transaction.getWalletCurrency())
        .status(transaction.getStatus())
        .createdAt(transaction.getCreatedAt())
        .fromWalletId(transaction.getWalletId())
        .toWalletId(transaction.getToWalletId())
        .usedLoanLimit(transaction.isUsedLoanLimit())
        .usedLoanLimitAmount(transaction.getUsedLoanLimitAmount())
        .commission(transaction.getCommission())
        .build();
  };
}
