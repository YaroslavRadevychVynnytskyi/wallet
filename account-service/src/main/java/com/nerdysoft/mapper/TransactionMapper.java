package com.nerdysoft.mapper;

import com.nerdysoft.config.MapperConfig;
import com.nerdysoft.dto.event.transaction.TransactionEvent;
import com.nerdysoft.dto.feign.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class)
public interface TransactionMapper {
    @Mapping(target = "timestamp", source = "createdAt")
    @Mapping(target = "transactionType", constant = "ACCOUNT_TO_ACCOUNT")
    TransactionEvent toTransactionEvent(Transaction transaction);
}
