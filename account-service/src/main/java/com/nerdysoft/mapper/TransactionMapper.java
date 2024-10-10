package com.nerdysoft.mapper;

import com.nerdysoft.config.MapperConfig;
import com.nerdysoft.dto.event.transaction.TransactionEvent;
import com.nerdysoft.dto.event.transaction.enums.TransactionType;
import com.nerdysoft.dto.feign.Transaction;
import com.nerdysoft.dto.feign.WalletTransactionResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class)
public interface TransactionMapper {
    @Mapping(target = "timestamp", source = "createdAt")
    @Mapping(target = "transactionType", constant = "ACCOUNT_TO_ACCOUNT")
    TransactionEvent toTransactionEvent(Transaction transaction);

    @Mapping(target = "timestamp", source = "responseDto.createdAt")
    @Mapping(target = "toWalletId", source = "responseDto.walletId")
    @Mapping(target = "transactionType", expression = "java(transactionType)")
    TransactionEvent toTransactionEvent(WalletTransactionResponseDto responseDto, TransactionType transactionType);
}
