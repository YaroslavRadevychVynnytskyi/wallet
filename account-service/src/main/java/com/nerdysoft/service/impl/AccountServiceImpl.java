package com.nerdysoft.service.impl;

import com.nerdysoft.dto.api.request.UpdateAccountRequestDto;
import com.nerdysoft.dto.event.activity.enums.ActionType;
import com.nerdysoft.dto.event.activity.enums.EntityType;
import com.nerdysoft.mapper.AccountMapper;
import com.nerdysoft.model.Account;
import com.nerdysoft.model.Role;
import com.nerdysoft.model.enums.RoleName;
import com.nerdysoft.repo.AccountRepository;
import com.nerdysoft.service.AccountService;
import com.nerdysoft.service.EventProducer;
import com.nerdysoft.service.RoleService;
import jakarta.persistence.EntityNotFoundException;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;
    private final PasswordEncoder passwordEncoder;
    private final RoleService roleService;
    private final EventProducer eventProducer;

    @Override
    public Account findById(UUID accountId) {
        Account account = accountRepository.findById(accountId).orElseThrow(() ->
            new EntityNotFoundException(String.format("Can't find account with ID: %s", accountId)));

        eventProducer.sendEvent(
            account.getAccountId(),
            account.getAccountId(),
            ActionType.READ,
            EntityType.ACCOUNT,
            Optional.empty(),
            Optional.empty());

        return account;
    }

    @Override
    public Account findByEmail(String email) {
        Account account = accountRepository.findByEmail(email).orElseThrow(() ->
            new EntityNotFoundException(String.format("Can't find account with email: %s", email)));

        eventProducer.sendEvent(
            account.getAccountId(),
            account.getAccountId(),
            ActionType.READ,
            EntityType.ACCOUNT,
            Optional.empty(),
            Optional.empty());

        return account;
    }

    @Override
    @Transactional
    public Account create(Account account) {
        account.setPassword(passwordEncoder.encode(account.getPassword()));
        Role role = roleService.findByName(RoleName.USER);
        account.getRoles().add(role);
        account = accountRepository.save(account);

        eventProducer.sendEvent(
            account.getAccountId(),
            account.getAccountId(),
            ActionType.CREATE,
            EntityType.ACCOUNT,
            Optional.empty(),
            Optional.of(account));

        return account;
    }

    @Override
    public Account update(UUID accountId, UpdateAccountRequestDto requestDto) {
        Account account = findById(accountId);
        Account oldAccount = accountMapper.clone(account);

        accountMapper.updateFromDto(account, requestDto);
        account = accountRepository.save(account);

        eventProducer.sendEvent(
                account.getAccountId(),
                account.getAccountId(),
                ActionType.UPDATE,
                EntityType.ACCOUNT,
                Optional.of(oldAccount),
                Optional.of(account));

        return account;
    }

    @Override
    public void deleteById(UUID accountId) {
        Account account = findById(accountId);
        accountRepository.deleteById(accountId);

        eventProducer.sendEvent(
                account.getAccountId(),
                account.getAccountId(),
                ActionType.DELETE,
                EntityType.ACCOUNT,
                Optional.of(account),
                Optional.empty());
    }
}
