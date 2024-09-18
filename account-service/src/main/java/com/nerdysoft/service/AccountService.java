package com.nerdysoft.service;

import com.nerdysoft.dto.request.CreateAccountRequestDto;
import com.nerdysoft.dto.request.CreateTransactionRequestDto;
import com.nerdysoft.dto.request.UpdateAccountRequestDto;
import com.nerdysoft.dto.response.AccountResponseDto;
import com.nerdysoft.dto.response.TransactionResponseDto;
import com.nerdysoft.dto.response.UpdatedAccountResponseDto;
import com.nerdysoft.entity.Account;
import java.util.UUID;

public interface AccountService {
    Account create(CreateAccountRequestDto requestDto);

    AccountResponseDto getById(UUID accountId);

    UpdatedAccountResponseDto update(UUID accountId, UpdateAccountRequestDto requestDto);

    void deleteById(UUID accountId);

    TransactionResponseDto createTransaction(UUID accountId, CreateTransactionRequestDto requestDto);

    Account findByEmail(String email);
}
