package com.nerdysoft.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nerdysoft.dto.feign.CreateWalletDto;
import com.nerdysoft.dto.feign.Currency;
import com.nerdysoft.dto.feign.Transaction;
import com.nerdysoft.dto.feign.TransactionStatus;
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
import com.nerdysoft.repo.AccountRepository;
import com.nerdysoft.service.impl.AccountServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {
    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AccountMapper accountMapper;

    @Mock
    private WalletFeignClient walletFeignClient;

    @InjectMocks
    private AccountServiceImpl accountService;

    @Test
    void create_WithCorrectCreateAccountRequestDto_ShouldReturnValidAccountResponseDto() {
        //Given
        CreateAccountRequestDto requestDto = new CreateAccountRequestDto(
                "John Doe",
                "john@email.com",
                "johnPassword123"
        );

        Account account = Account.builder()
                .username(requestDto.username())
                .email(requestDto.email())
                .password(requestDto.password())
                .build();

        UUID accountMockId = UUID.randomUUID();

        CreateWalletDto createWalletDto = new CreateWalletDto(
                accountMockId,
                Currency.USD
        );

        LocalDateTime now = LocalDateTime.now();

        Wallet wallet = new Wallet(
                UUID.randomUUID(),
                accountMockId,
                BigDecimal.valueOf(0),
                Currency.USD,
                now
        );

        AccountResponseDto expected = new AccountResponseDto(
                account.getAccountId(),
                account.getUsername(),
                account.getEmail(),
                account.getCreatedAt()
        );



        when(accountMapper.toModel(requestDto)).thenReturn(account);

        account.setAccountId(accountMockId);
        when(accountRepository.save(account)).thenReturn(account);
        when(walletFeignClient.createWallet(createWalletDto))
                .thenReturn(ResponseEntity.ofNullable(wallet));
        when(accountMapper.toDto(account)).thenReturn(expected);

        //When
        AccountResponseDto actual = accountService.create(requestDto);

        //Then
        assertEquals(expected, actual);

        verify(accountMapper, times(1)).toModel(requestDto);
        verify(accountRepository, times(1)).save(account);
        verify(walletFeignClient, times(1)).createWallet(createWalletDto);
        verify(accountMapper, times(1)).toDto(account);
    }

    @Test
    void getById_ExistingId_ShouldReturnValidAccountResponseDto() {
        //Given
        UUID accountMockId = UUID.randomUUID();

        Account account = Account.builder()
                .accountId(accountMockId)
                .username("John Peters")
                .email("john@email.com")
                .password("123john56")
                .build();

        AccountResponseDto expected = new AccountResponseDto(
                account.getAccountId(),
                account.getUsername(),
                account.getEmail(),
                account.getCreatedAt()
        );

        when(accountRepository.findById(accountMockId)).thenReturn(Optional.of(account));
        when(accountMapper.toDto(account)).thenReturn(expected);

        //When
        AccountResponseDto actual = accountService.getById(accountMockId);

        //Then
        assertEquals(expected, actual);

        verify(accountRepository, times(1)).findById(accountMockId);
        verify(accountMapper, times(1)).toDto(account);
    }

    @Test
    void getById_InvalidId_ShouldThrowEntityNotFoundException() {
        //Given
        UUID invalidMockId = UUID.randomUUID();

        when(accountRepository.findById(invalidMockId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            accountService.getById(invalidMockId);
        });

        verify(accountRepository, times(1)).findById(invalidMockId);
    }

    @Test
    void update_WithCorrectInputData_ShouldReturnCorrectUpdatedAccountResponseDto() {
        //Given
        UUID accountMockId = UUID.randomUUID();

        UpdateAccountRequestDto requestDto = new UpdateAccountRequestDto(
                "Peter Smith",
                "peter.smith@email.com"
        );

        Account account = Account.builder()
                .accountId(accountMockId)
                .username("Peter Jackson")
                .email("peter@email.com")
                .password("1234peter56")
                .build();

        UpdatedAccountResponseDto expected = new UpdatedAccountResponseDto(
                accountMockId,
                "Peter Smith",
                "peter.smith@email.com",
                LocalDateTime.now()
        );

        when(accountRepository.findById(accountMockId)).thenReturn(Optional.of(account));

        doNothing().when(accountMapper).updateFromDto(account, requestDto);
        account.setUsername("Peter Smith");
        account.setEmail("peter.smith@email.com");

        when(accountRepository.save(account)).thenReturn(account);
        when(accountMapper.toUpdateResponseDto(account)).thenReturn(expected);

        //When
        UpdatedAccountResponseDto actual = accountService.update(accountMockId, requestDto);

        //Then
        assertEquals(expected, actual);

        verify(accountRepository, times(1)).findById(accountMockId);
        verify(accountRepository, times(1)).save(account);
        verify(accountMapper, times(1)).toUpdateResponseDto(account);
    }

    @Test
    void deleteById_ExistingId_ShouldDelete() {
        //Given
        UUID accountMockId = UUID.randomUUID();

        //When
        accountService.deleteById(accountMockId);

        //Then
        verify(accountRepository, times(1)).deleteById(accountMockId);
    }

    @Test
    void createTransaction_AllOk_ShouldReturnCorrectTransactionResponseDto() {
        //Given
        UUID fromAccountId = UUID.randomUUID();
        UUID toAccountId = UUID.randomUUID();
        Currency currency = Currency.USD;

        CreateTransactionRequestDto requestDto = new CreateTransactionRequestDto(
                toAccountId,
                BigDecimal.valueOf(160),
                currency
        );

        UUID fromWalletId = UUID.randomUUID();
        UUID toWalletId = UUID.randomUUID();

        Wallet fromWallet = new Wallet(
                fromWalletId,
                fromAccountId,
                BigDecimal.valueOf(4200),
                currency,
                LocalDateTime.of(2019, Month.AUGUST, 4, 0, 0)
        );

        Wallet toWallet = new Wallet(
                toWalletId,
                toAccountId,
                BigDecimal.valueOf(2000),
                currency,
                LocalDateTime.of(2021, Month.JUNE, 28, 0, 0)
        );

        TransferRequestDto transferRequestDto = new TransferRequestDto(
                toWalletId,
                BigDecimal.valueOf(160),
                currency
        );

        Transaction transaction = new Transaction(
                UUID.randomUUID(),
                fromWallet.getWalletId(),
                toWallet.getWalletId(),
                transferRequestDto.amount(),
                currency,
                TransactionStatus.SUCCESS,
                LocalDateTime.now()
        );

        TransactionResponseDto expected = new TransactionResponseDto(
                transaction,
                fromAccountId,
                toAccountId
        );

        when(walletFeignClient.getWalletByAccountIdAndCurrency(fromAccountId, currency))
                .thenReturn(ResponseEntity.ofNullable(fromWallet));

        when(walletFeignClient.getWalletByAccountIdAndCurrency(toAccountId, currency))
                .thenReturn(ResponseEntity.ofNullable(toWallet));

        when(walletFeignClient.transfer(fromWallet.getWalletId(), transferRequestDto))
                .thenReturn(ResponseEntity.ofNullable(transaction));

        //When
        TransactionResponseDto transactionResponseDto = accountService
                .createTransaction(fromAccountId, requestDto);

        //Then
        assertEquals(expected, transactionResponseDto);

        verify(walletFeignClient, times(1)).getWalletByAccountIdAndCurrency(fromAccountId, currency);
        verify(walletFeignClient, times(1)).getWalletByAccountIdAndCurrency(toAccountId, currency);
        verify(walletFeignClient, times(1)).transfer(fromWallet.getWalletId(), transferRequestDto);
    }
}
