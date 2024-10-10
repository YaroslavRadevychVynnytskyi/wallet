package com.nerdysoft.service.impl;

import com.nerdysoft.dto.api.request.CreateAccountRequestDto;
import com.nerdysoft.dto.api.request.CreateTransactionRequestDto;
import com.nerdysoft.dto.api.request.GenericTransactionRequestDto;
import com.nerdysoft.dto.api.request.UpdateAccountRequestDto;
import com.nerdysoft.dto.api.response.AccountResponseDto;
import com.nerdysoft.dto.api.response.GenericTransactionResponseDto;
import com.nerdysoft.dto.api.response.TransactionResponseDto;
import com.nerdysoft.dto.api.response.UpdatedAccountResponseDto;
import com.nerdysoft.dto.event.activity.enums.ActionType;
import com.nerdysoft.dto.event.activity.enums.EntityType;
import com.nerdysoft.dto.feign.CreateWalletDto;
import com.nerdysoft.dto.feign.Currency;
import com.nerdysoft.dto.feign.Transaction;
import com.nerdysoft.dto.feign.TransferRequestDto;
import com.nerdysoft.dto.feign.Wallet;
import com.nerdysoft.dto.feign.WalletTransactionRequestDto;
import com.nerdysoft.dto.feign.WalletTransactionResponseDto;
import com.nerdysoft.entity.Account;
import com.nerdysoft.entity.Role;
import com.nerdysoft.entity.enums.RoleName;
import com.nerdysoft.feign.WalletFeignClient;
import com.nerdysoft.mapper.AccountMapper;
import com.nerdysoft.mapper.TransactionMapper;
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
    private final WalletFeignClient walletFeignClient;
    private final EventProducer eventProducer;
    private final TransactionMapper transactionMapper;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public Account create(CreateAccountRequestDto requestDto) {
        Account account = accountMapper.toModel(requestDto);
        account.setPassword(passwordEncoder.encode(requestDto.password()));

        Role role = roleService.getRoleByName(RoleName.USER);
        account.getRoles().add(role);
        account = accountRepository.save(account);

        CreateWalletDto walletDto = new CreateWalletDto(account.getAccountId(), Currency.USD);
        walletFeignClient.createWallet(walletDto);

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
    public AccountResponseDto getById(UUID accountId) {
        Account account = retrieveById(accountId);

        eventProducer.sendEvent(
                account.getAccountId(),
                account.getAccountId(),
                ActionType.READ,
                EntityType.ACCOUNT,
                Optional.empty(),
                Optional.empty());

        return accountMapper.toDto(account);
    }

    @Override
    public UpdatedAccountResponseDto update(UUID accountId, UpdateAccountRequestDto requestDto) {
        Account account = retrieveById(accountId);
        Account oldAccount = accountMapper.clone(account);

        accountMapper.updateFromDto(account, requestDto);
        Account updatedAccount = accountRepository.save(account);

        eventProducer.sendEvent(
                account.getAccountId(),
                account.getAccountId(),
                ActionType.UPDATE,
                EntityType.ACCOUNT,
                Optional.of(oldAccount),
                Optional.of(updatedAccount));

        return accountMapper.toUpdateResponseDto(updatedAccount);
    }

    @Override
    public void deleteById(UUID accountId) {
        Account account = retrieveById(accountId);
        accountRepository.deleteById(accountId);

        eventProducer.sendEvent(
                account.getAccountId(),
                account.getAccountId(),
                ActionType.DELETE,
                EntityType.ACCOUNT,
                Optional.of(account),
                Optional.empty());
    }

    @Override
    public TransactionResponseDto createTransaction(UUID accountId,
                                                    CreateTransactionRequestDto requestDto,
                                                    Currency fromWalletCurrency,
                                                    Currency toWalletCurrency) {
        Wallet fromWallet = walletFeignClient.getWalletByAccountIdAndCurrency(
                accountId,
                fromWalletCurrency)
                .getBody();

        Wallet toWallet = walletFeignClient.getWalletByAccountIdAndCurrency(
                requestDto.toAccountId(),
                toWalletCurrency)
                .getBody();

        TransferRequestDto transferRequestDto = new TransferRequestDto(
                toWallet.getWalletId(),
                requestDto.amount(),
                requestDto.currency()
        );

        Transaction transaction = walletFeignClient.transfer(
                fromWallet.getWalletId(),
                transferRequestDto)
                .getBody();

        eventProducer.sendEvent(transactionMapper.toTransactionEvent(transaction));
        eventProducer.sendEvent(
                accountId,
                transaction.transactionId(),
                ActionType.CREATE,
                EntityType.TRANSACTION,
                Optional.empty(),
                Optional.of(transaction));

        return new TransactionResponseDto(transaction, accountId, requestDto.toAccountId());
    }

    @Override
    public GenericTransactionResponseDto updateBalance(UUID accountId, GenericTransactionRequestDto requestDto) {
        Wallet wallet = walletFeignClient.getWalletByAccountIdAndCurrency(accountId, requestDto.walletCurrency()).getBody();

        WalletTransactionResponseDto walletTransactionResponseDto = processUpdateBalanceTransaction(wallet, requestDto);

        eventProducer.sendEvent(transactionMapper.toTransactionEvent(walletTransactionResponseDto, requestDto.transactionType()));
        eventProducer.sendEvent(
                accountId,
                walletTransactionResponseDto.transactionId(),
                ActionType.CREATE,
                EntityType.TRANSACTION,
                Optional.empty(),
                Optional.of(transactionMapper.toTransactionEvent(walletTransactionResponseDto, requestDto.transactionType()))
        );

        return new GenericTransactionResponseDto(walletTransactionResponseDto, accountId);
    }

    private WalletTransactionResponseDto processUpdateBalanceTransaction(Wallet wallet, GenericTransactionRequestDto requestDto) {
        WalletTransactionRequestDto transactionRequestDto = new WalletTransactionRequestDto(requestDto.amount(), requestDto.currency());

        switch (requestDto.transactionType()) {
            case ACCOUNT_DEPOSIT -> {
                return walletFeignClient.deposit(wallet.getWalletId(), transactionRequestDto).getBody();
            }
            case ACCOUNT_WITHDRAW -> {
                return walletFeignClient.withdraw(wallet.getWalletId(), transactionRequestDto).getBody();
            }
            default -> throw new IllegalArgumentException("Unsupported operation type" + requestDto.transactionType());
        }
    }

    @Override
    public Account findByEmail(String email) {
        return accountRepository.findByEmail(email).orElseThrow(EntityNotFoundException::new);
    }

    private Account retrieveById(UUID accountId) {
        return accountRepository.findById(accountId).orElseThrow(() ->
                new EntityNotFoundException("Can't find account with ID: " + accountId));
    }
}
