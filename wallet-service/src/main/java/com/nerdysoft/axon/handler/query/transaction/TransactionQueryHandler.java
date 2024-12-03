package com.nerdysoft.axon.handler.query.transaction;

import com.nerdysoft.axon.query.transaction.FindAllAccountTransactionByLastMonthQuery;
import com.nerdysoft.axon.query.transaction.FindTransactionByIdQuery;
import com.nerdysoft.dto.transaction.TransactionDto;
import com.nerdysoft.entity.Transaction;
import com.nerdysoft.service.TransactionService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TransactionQueryHandler {
  private final TransactionService transactionService;

  @QueryHandler
  public Transaction handle(FindTransactionByIdQuery query) {
    return transactionService.findById(query.getTransactionId());
  }

  @QueryHandler
  public List<TransactionDto> handle(FindAllAccountTransactionByLastMonthQuery query) {
    return transactionService.findSuccessfulTransactionsByAccountIdInLastMonth(query.getAccountId()).stream()
        .map(t -> {
          TransactionDto dto = new TransactionDto();
          BeanUtils.copyProperties(t, dto);
          return dto;
        })
        .toList();
  }
}
