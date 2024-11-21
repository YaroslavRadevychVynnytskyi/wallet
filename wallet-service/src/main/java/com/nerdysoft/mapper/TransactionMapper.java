package com.nerdysoft.mapper;

import com.nerdysoft.config.MapperConfig;
import com.nerdysoft.dto.response.DepositResponseDto;
import com.nerdysoft.dto.response.TransferResponseDto;
import com.nerdysoft.dto.response.WithdrawResponseDto;
import com.nerdysoft.entity.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class)
public interface TransactionMapper {
  DepositResponseDto toDepositResponseDto(Transaction transaction);

  WithdrawResponseDto toWithdrawResponseDto(Transaction transaction);

  @Mapping(source = "walletId", target = "fromWalletId")
  TransferResponseDto toTransferResponseDto(Transaction transaction);
}
