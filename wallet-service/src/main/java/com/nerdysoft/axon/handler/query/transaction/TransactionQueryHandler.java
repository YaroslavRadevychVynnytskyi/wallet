package com.nerdysoft.axon.handler.query.transaction;

import com.nerdysoft.axon.query.transaction.FindTransactionByIdQuery;
import com.nerdysoft.entity.Transaction;
import com.nerdysoft.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TransactionQueryHandler {
  private final TransactionService transactionService;

  @QueryHandler
  public Transaction handle(FindTransactionByIdQuery query) {
    return transactionService.findById(query.getTransactionId());
  }
}
