package com.nerdysoft.axon.handler.query;

import com.nerdysoft.axon.query.transaction.FindAllTransactionsByWalletIdQuery;
import com.nerdysoft.model.Transaction;
import com.nerdysoft.service.TransactionService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TransactionQueryHandler {
  private final TransactionService transactionService;

  @QueryHandler
  public List<Transaction> handle(FindAllTransactionsByWalletIdQuery query) {
    return transactionService.findAllByWalletId(query.getWalletId());
  }
}
