package com.nerdysoft.service;

import com.nerdysoft.dto.api.request.CreateAccountRequestDto;
import com.nerdysoft.dto.api.request.CreateTransactionRequestDto;
import com.nerdysoft.dto.api.request.GenericTransactionRequestDto;
import com.nerdysoft.dto.api.request.UpdateAccountRequestDto;
import com.nerdysoft.dto.api.response.AccountResponseDto;
import com.nerdysoft.dto.api.response.GenericTransactionResponseDto;
import com.nerdysoft.dto.api.response.TransactionResponseDto;
import com.nerdysoft.dto.api.response.UpdatedAccountResponseDto;
import com.nerdysoft.entity.Account;
import com.nerdysoft.model.enums.Currency;
import java.util.UUID;

public interface AccountService {
    Account create(CreateAccountRequestDto requestDto);

    AccountResponseDto getById(UUID accountId);

    UpdatedAccountResponseDto update(UUID accountId, UpdateAccountRequestDto requestDto);

    void deleteById(UUID accountId);

    TransactionResponseDto createTransaction(UUID accountId, CreateTransactionRequestDto requestDto, Currency fromWalletCurrency, Currency toWalletCurrency);

    Account findByEmail(String email);

    GenericTransactionResponseDto updateBalance(UUID accountId, GenericTransactionRequestDto requestDto);
}
