package com.nerdysoft.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import com.nerdysoft.dto.feign.TransactionStatus;
import com.nerdysoft.dto.feign.TransferRequestDto;
import com.nerdysoft.dto.feign.Wallet;
import com.nerdysoft.entity.Account;
import com.nerdysoft.entity.Role;
import com.nerdysoft.entity.enums.RoleName;
import com.nerdysoft.feign.ApiGatewayFeignClient;
import com.nerdysoft.mapper.AccountMapper;
import com.nerdysoft.mapper.TransactionMapper;
import com.nerdysoft.repo.AccountRepository;
import com.nerdysoft.service.impl.AccountServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
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
    private ApiGatewayFeignClient apiGatewayFeignClient;

    @Mock
    private TransactionMapper transactionMapper;

    @Mock
    private EventProducer eventProducer;

    @Mock
    private RoleService roleService;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AccountServiceImpl accountService;

    @Test
    public void shouldCreateAccount() {
        CreateAccountRequestDto createAccountRequestDto = new CreateAccountRequestDto("Test", "email@test.com", "password");
        Account account = new Account(UUID.randomUUID(), "Test", "email@test.com", "password");
        Role role = new Role(1, RoleName.USER);
        when(accountMapper.toModel(createAccountRequestDto)).thenReturn(account);
        when(roleService.getRoleByName(RoleName.USER)).thenReturn(role);
        when(accountRepository.save(any(Account.class))).thenReturn(account);
        Account result = accountService.create(createAccountRequestDto);
        assertEquals(1, result.getRoles().size());
        verify(eventProducer, times(1)).sendEvent(account.getAccountId(),
            account.getAccountId(), ActionType.CREATE, EntityType.ACCOUNT, Optional.empty(),
            Optional.of(account));
        verify(apiGatewayFeignClient, times(1)).createWallet(any(CreateWalletDto.class));
    }

    @Test
    void getById_ExistingId_ShouldReturnValidAccountResponseDto() {
        //Given
        UUID accountMockId = UUID.randomUUID();

        Account account = Account.builder()
                .accountId(accountMockId)
                .fullName("John Peters")
                .email("john@email.com")
                .password("123john56")
                .build();

        AccountResponseDto expected = AccountResponseDto.builder()
            .accountId(account.getAccountId())
            .fullName(account.getFullName())
            .email(account.getEmail())
            .createdAt(account.getCreatedAt())
            .build();

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
                .fullName("Peter Jackson")
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
        account.setFullName("Peter Smith");
        account.setEmail("peter.smith@email.com");

        when(accountRepository.save(account)).thenReturn(account);
        when(accountMapper.toUpdateResponseDto(account)).thenReturn(expected);
        when(accountMapper.clone(account)).thenReturn(new Account());

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
        UUID accountId = UUID.randomUUID();

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(new Account()));
        //When
        accountService.deleteById(accountId);

        //Then
        verify(accountRepository, times(1)).deleteById(accountId);
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

        when(apiGatewayFeignClient.getWalletByAccountIdAndCurrency(fromAccountId, currency))
                .thenReturn(ResponseEntity.ofNullable(fromWallet));

        when(apiGatewayFeignClient.getWalletByAccountIdAndCurrency(toAccountId, currency))
                .thenReturn(ResponseEntity.ofNullable(toWallet));

        when(apiGatewayFeignClient.transfer(fromWallet.getWalletId(), transferRequestDto))
                .thenReturn(ResponseEntity.ofNullable(transaction));

        //When
        TransactionResponseDto transactionResponseDto = accountService
                .createTransaction(fromAccountId, requestDto);

        //Then
        assertEquals(expected, transactionResponseDto);

        verify(apiGatewayFeignClient, times(1)).getWalletByAccountIdAndCurrency(fromAccountId, currency);
        verify(apiGatewayFeignClient, times(1)).getWalletByAccountIdAndCurrency(toAccountId, currency);
        verify(apiGatewayFeignClient, times(1)).transfer(fromWallet.getWalletId(), transferRequestDto);
    }

    @Test
    public void shouldFindAccountByEmail() {
        final String EMAIL = "email@test.com";
        final Account ACCOUNT = new Account(UUID.randomUUID(), "Test", EMAIL, "password");
        when(accountRepository.findByEmail(EMAIL)).thenReturn(Optional.of(ACCOUNT));
        assertDoesNotThrow(() -> accountService.findByEmail(EMAIL));
    }

    @Test
    public void shouldThrowExceptionWhenAccountNotFoundByEmail() {
        final String EMAIL = "email@test.com";
        when(accountRepository.findByEmail(EMAIL)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> accountService.findByEmail(EMAIL));
    }
}
