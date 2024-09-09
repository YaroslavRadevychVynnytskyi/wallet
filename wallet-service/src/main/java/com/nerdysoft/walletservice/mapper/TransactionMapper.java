package com.nerdysoft.walletservice.mapper;

import com.nerdysoft.walletservice.config.MapperConfig;
import com.nerdysoft.walletservice.dto.response.TransactionResponseDto;
import com.nerdysoft.walletservice.dto.response.TransferResponseDto;
import com.nerdysoft.walletservice.model.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class)
public interface TransactionMapper {
  TransactionResponseDto transactionToTransactionResponseDto(Transaction transaction);

  @Mapping(source = "walletId", target = "fromWalletId")
  TransferResponseDto transactionToTransferResponseDto(Transaction transaction);
}
