package com.nerdysoft.walletservice.mapper;

import com.nerdysoft.walletservice.config.MapperConfig;
import com.nerdysoft.walletservice.dto.response.TransactionDto;
import com.nerdysoft.walletservice.model.Transaction;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface TransactionMapper {
  TransactionDto transactionToTransactionDto(Transaction transaction);
}
