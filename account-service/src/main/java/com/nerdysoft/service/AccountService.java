package com.nerdysoft.service;

import com.nerdysoft.dto.api.request.CreateAccountRequestDto;
import com.nerdysoft.dto.api.request.CreateTransactionRequestDto;
import com.nerdysoft.dto.api.request.UpdateAccountRequestDto;
import com.nerdysoft.dto.api.response.AccountResponseDto;
import com.nerdysoft.dto.api.response.TransactionResponseDto;
import com.nerdysoft.dto.api.response.UpdatedAccountResponseDto;
import java.util.UUID;

public interface AccountService {
    AccountResponseDto create(CreateAccountRequestDto requestDto);

    AccountResponseDto getById(UUID accountId);

    UpdatedAccountResponseDto update(UUID accountId, UpdateAccountRequestDto requestDto);

    void deleteById(UUID accountId);

    TransactionResponseDto createTransaction(UUID accountId, CreateTransactionRequestDto requestDto);
}
