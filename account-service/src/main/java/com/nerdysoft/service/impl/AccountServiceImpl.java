package com.nerdysoft.service.impl;

import com.nerdysoft.dto.feign.CreateWalletDto;
import com.nerdysoft.dto.feign.Currency;
import com.nerdysoft.dto.feign.Transaction;
import com.nerdysoft.dto.feign.TransferRequestDto;
import com.nerdysoft.dto.feign.Wallet;
import com.nerdysoft.dto.request.CreateAccountRequestDto;
import com.nerdysoft.dto.request.CreateTransactionRequestDto;
import com.nerdysoft.dto.request.UpdateAccountRequestDto;
import com.nerdysoft.dto.response.AccountResponseDto;
import com.nerdysoft.dto.response.TransactionResponseDto;
import com.nerdysoft.dto.response.UpdatedAccountResponseDto;
import com.nerdysoft.entity.Account;
import com.nerdysoft.feign.WalletFeignClient;
import com.nerdysoft.mapper.AccountMapper;
import com.nerdysoft.mapper.TransactionMapper;
import com.nerdysoft.repo.AccountRepository;
import com.nerdysoft.service.AccountService;
import com.nerdysoft.service.EventProducer;
import jakarta.persistence.EntityNotFoundException;
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

        eventProducer.sendTransactionEvent(transactionMapper.toTransactionEvent(transaction));

        return new TransactionResponseDto(transaction, accountId, requestDto.toAccountId());
    }

    private Account retrieveById(UUID accountId) {
        return accountRepository.findById(accountId).orElseThrow(() ->
                new EntityNotFoundException("Can't find account with ID: " + accountId));
    }
}
