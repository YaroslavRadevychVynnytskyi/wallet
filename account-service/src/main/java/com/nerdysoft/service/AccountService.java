package com.nerdysoft.service;

import com.nerdysoft.dto.request.CreateAccountRequestDto;
import com.nerdysoft.dto.request.CreateTransactionRequestDto;
import com.nerdysoft.dto.request.UpdateAccountRequestDto;
import com.nerdysoft.dto.response.AccountResponseDto;
import com.nerdysoft.dto.response.TransactionResponseDto;
import com.nerdysoft.dto.response.UpdatedAccountResponseDto;
import java.util.UUID;

public interface AccountService {
    AccountResponseDto create(CreateAccountRequestDto requestDto);

    AccountResponseDto getById(UUID accountId);

    UpdatedAccountResponseDto update(UUID accountId, UpdateAccountRequestDto requestDto);

    void deleteById(UUID accountId);

    TransactionResponseDto createTransaction(UUID accountId, CreateTransactionRequestDto requestDto);
}
