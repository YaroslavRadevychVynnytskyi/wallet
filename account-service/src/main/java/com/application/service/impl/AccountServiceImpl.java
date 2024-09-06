package com.application.service.impl;

import com.application.dto.AccountResponseDto;
import com.application.dto.CreateAccountRequestDto;
import com.application.dto.UpdateAccountRequestDto;
import com.application.dto.UpdatedAccountResponseDto;
import com.application.entity.Account;
import com.application.mapper.AccountMapper;
import com.application.repo.AccountRepository;
import com.application.service.AccountService;
import jakarta.persistence.EntityNotFoundException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;

    @Override
    public AccountResponseDto create(CreateAccountRequestDto requestDto) {
        Account savedAccount = accountRepository.save(accountMapper.toModel(requestDto));
        return accountMapper.toDto(savedAccount);
    }

    @Override
    public AccountResponseDto getById(UUID accountId) {
        Account account = retrieveById(accountId);
        return accountMapper.toDto(account);
    }

    @Override
    public UpdatedAccountResponseDto update(UUID accountId, UpdateAccountRequestDto requestDto) {
        Account account = retrieveById(accountId);
        accountMapper.updateFromDto(account, requestDto);
        Account updatedAccount = accountRepository.save(account);

        return accountMapper.toUpdateResponseDto(updatedAccount);
    }

    @Override
    public void deleteById(UUID accountId) {
        accountRepository.deleteById(accountId);
    }

    private Account retrieveById(UUID accountId) {
        return accountRepository.findById(accountId).orElseThrow(() ->
                new EntityNotFoundException("Can't find account with ID: " + accountId));
    }
}
