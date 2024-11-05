package com.nerdysoft.service;

import com.nerdysoft.dto.api.request.CreateTransactionRequestDto;
import com.nerdysoft.dto.api.request.GenericTransactionRequestDto;
import com.nerdysoft.dto.api.response.GenericTransactionResponseDto;
import com.nerdysoft.dto.api.response.TransactionResponseDto;
import com.nerdysoft.model.enums.Currency;
import java.util.UUID;

public interface TransactionService {
  TransactionResponseDto createTransaction(UUID accountId, CreateTransactionRequestDto dto,
      Currency fromWalletCurrency, Currency toWalletCurrency);

  GenericTransactionResponseDto updateBalance(UUID accountId, GenericTransactionRequestDto dto);
}
