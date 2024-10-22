package com.nerdysoft.mapper;

import com.nerdysoft.dto.response.TransactionResponseDto;
import com.nerdysoft.dto.response.TransferResponseDto;
import com.nerdysoft.config.MapperConfig;
import com.nerdysoft.entity.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class)
public interface TransactionMapper {
  TransactionResponseDto transactionToTransactionResponseDto(Transaction transaction);

  @Mapping(source = "walletId", target = "fromWalletId")
  TransferResponseDto transactionToTransferResponseDto(Transaction transaction);
}
