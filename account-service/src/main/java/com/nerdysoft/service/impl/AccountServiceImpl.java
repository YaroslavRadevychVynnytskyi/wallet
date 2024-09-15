package com.nerdysoft.service.impl;

import com.nerdysoft.dto.api.request.CreateAccountRequestDto;
import com.nerdysoft.dto.api.request.CreateTransactionRequestDto;
import com.nerdysoft.dto.api.request.UpdateAccountRequestDto;
import com.nerdysoft.dto.api.response.AccountResponseDto;
import com.nerdysoft.dto.api.response.TransactionResponseDto;
import com.nerdysoft.dto.api.response.UpdatedAccountResponseDto;
import com.nerdysoft.dto.event.activity.enums.ActionType;
import com.nerdysoft.dto.event.activity.enums.EntityType;
import com.nerdysoft.dto.feign.CreateWalletDto;
import com.nerdysoft.dto.feign.Currency;
import com.nerdysoft.dto.feign.Transaction;
import com.nerdysoft.dto.feign.TransferRequestDto;
import com.nerdysoft.dto.feign.Wallet;
import com.nerdysoft.entity.Account;
import com.nerdysoft.feign.WalletFeignClient;
import com.nerdysoft.mapper.AccountMapper;
import com.nerdysoft.mapper.TransactionMapper;
import com.nerdysoft.repo.AccountRepository;
import com.nerdysoft.service.AccountService;
import com.nerdysoft.service.EventProducer;
import jakarta.persistence.EntityNotFoundException;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;
    private final WalletFeignClient walletFeignClient;
    private final EventProducer eventProducer;
    private final TransactionMapper transactionMapper;

    @Override
    public AccountResponseDto create(CreateAccountRequestDto requestDto) {
        Account savedAccount = accountRepository.save(accountMapper.toModel(requestDto));

        CreateWalletDto walletDto = new CreateWalletDto(savedAccount.getAccountId(), Currency.USD);
        walletFeignClient.createWallet(walletDto);

        eventProducer.sendEvent(
                savedAccount.getAccountId(),
                savedAccount.getAccountId(),
                ActionType.CREATE,
                EntityType.ACCOUNT,
                Optional.empty(),
                Optional.of(savedAccount));

        return accountMapper.toDto(savedAccount);
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
                                                    CreateTransactionRequestDto requestDto) {
        Wallet fromWallet = walletFeignClient.getWalletByAccountIdAndCurrency(
                        accountId,
                        requestDto.currency())
                .getBody();

        Wallet toWallet = walletFeignClient.getWalletByAccountIdAndCurrency(
                        requestDto.toAccountId(),
                        requestDto.currency())
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

    private Account retrieveById(UUID accountId) {
        return accountRepository.findById(accountId).orElseThrow(() ->
                new EntityNotFoundException("Can't find account with ID: " + accountId));
    }
}
