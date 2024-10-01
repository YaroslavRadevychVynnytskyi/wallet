package com.nerdysoft.service;

import com.nerdysoft.dto.api.request.CreateAccountRequestDto;
import com.nerdysoft.dto.api.request.CreateTransactionRequestDto;
import com.nerdysoft.dto.api.request.UpdateAccountRequestDto;
import com.nerdysoft.dto.api.response.AccountResponseDto;
import com.nerdysoft.dto.api.response.TransactionResponseDto;
import com.nerdysoft.dto.api.response.UpdatedAccountResponseDto;
import com.nerdysoft.dto.feign.Currency;
import com.nerdysoft.entity.Account;
import java.util.UUID;

public interface AccountService {
    Account create(CreateAccountRequestDto requestDto);

    AccountResponseDto getById(UUID accountId);

    UpdatedAccountResponseDto update(UUID accountId, UpdateAccountRequestDto requestDto);

    void deleteById(UUID accountId);

    TransactionResponseDto createTransaction(UUID accountId, CreateTransactionRequestDto requestDto, Currency fromWalletCurrency, Currency toWalletCurrency);

    Account findByEmail(String email);
}
