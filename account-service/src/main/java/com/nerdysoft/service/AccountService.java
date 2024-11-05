package com.nerdysoft.service;

import com.nerdysoft.dto.api.request.UpdateAccountRequestDto;
import com.nerdysoft.entity.Account;
import java.util.UUID;

public interface AccountService {
    Account findById(UUID accountId);

    Account findByEmail(String email);

    Account create(Account account);

    Account update(UUID accountId, UpdateAccountRequestDto requestDto);

    void deleteById(UUID accountId);
}
