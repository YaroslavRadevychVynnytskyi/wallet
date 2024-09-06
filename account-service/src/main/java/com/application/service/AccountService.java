package com.application.service;

import com.application.dto.AccountResponseDto;
import com.application.dto.CreateAccountRequestDto;
import com.application.dto.UpdateAccountRequestDto;
import com.application.dto.UpdatedAccountResponseDto;
import java.util.UUID;

public interface AccountService {
    AccountResponseDto create(CreateAccountRequestDto requestDto);

    AccountResponseDto getById(UUID accountId);

    UpdatedAccountResponseDto update(UUID accountId, UpdateAccountRequestDto requestDto);

    void deleteById(UUID accountId);
}
