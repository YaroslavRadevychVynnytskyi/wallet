package com.nerdysoft.walletservice.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nerdysoft.walletservice.dto.request.CreateWalletDto;
import com.nerdysoft.walletservice.dto.request.TransactionRequestDto;
import com.nerdysoft.walletservice.dto.request.TransferRequestDto;
import com.nerdysoft.walletservice.dto.response.TransactionResponseDto;
import com.nerdysoft.walletservice.dto.response.TransferResponseDto;
import com.nerdysoft.walletservice.exception.AccountHasAlreadyWalletOnThisCurrencyException;
import com.nerdysoft.walletservice.mapper.TransactionMapper;
import com.nerdysoft.walletservice.model.Transaction;
import com.nerdysoft.walletservice.model.Wallet;
import com.nerdysoft.walletservice.model.enums.Currency;
import com.nerdysoft.walletservice.model.enums.TransactionStatus;
import com.nerdysoft.walletservice.repository.WalletRepository;
import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class WalletServiceTest {
  @Mock
  private WalletRepository walletRepository;

  @Mock
  private TransactionService transactionService;

  @Mock
  private TransactionMapper transactionMapper;

  @InjectMocks
  private WalletService walletService;

  private final UUID uuid = UUID.randomUUID();

  private AutoCloseable mocks;

  @BeforeEach
  public void setUp() {
    mocks = MockitoAnnotations.openMocks(this);
  }

  @AfterEach
  public void tearDown() {
    try {
      mocks.close();
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

  @Test
  public void shouldCreateWallet() {
    CreateWalletDto createWalletDto = new CreateWalletDto(uuid, Currency.USD);
    Wallet wallet = new Wallet(createWalletDto);
    when(walletRepository.hasAccountWalletOnThisCurrency(createWalletDto.accountId(), createWalletDto.currency())).thenReturn(false);
    when(walletRepository.save(any(Wallet.class))).thenReturn(wallet);
    assertDoesNotThrow(() -> walletService.createWallet(createWalletDto));
    verify(walletRepository, times(1)).save(any(Wallet.class));
  }

  @Test
  public void shouldThrowExceptionWhenAccountTriesToCreateAnotherWalletOnSameCurrency() {
    CreateWalletDto createWalletDto = new CreateWalletDto(uuid, Currency.USD);
    when(walletRepository.hasAccountWalletOnThisCurrency(createWalletDto.accountId(), createWalletDto.currency())).thenReturn(true);
    assertThrows(AccountHasAlreadyWalletOnThisCurrencyException.class, () -> walletService.createWallet(createWalletDto));
    verify(walletRepository, times(0)).save(any(Wallet.class));
  }

  @Test
  public void shouldFindWallet() {
    CreateWalletDto createWalletDto = new CreateWalletDto(uuid, Currency.USD);
    Wallet wallet = new Wallet(createWalletDto);
    when(walletRepository.findById(uuid)).thenReturn(Optional.of(wallet));
    assertDoesNotThrow(() -> walletService.getWallet(uuid));
  }

  @Test
  public void shouldThrowExceptionWhenWalletNotFound() {
    when(walletRepository.findById(uuid)).thenReturn(Optional.empty());
    assertThrows(EntityNotFoundException.class, () -> walletService.getWallet(uuid));
  }

  @Test
  public void shouldUpdateCurrency() {
    CreateWalletDto createWalletDto1 = new CreateWalletDto(uuid, Currency.USD);
    Wallet wallet1 = new Wallet(createWalletDto1);
    CreateWalletDto createWalletDto2 = new CreateWalletDto(uuid, Currency.UAH);
    Wallet wallet2 = new Wallet(createWalletDto2);
    when(walletRepository.findById(uuid)).thenReturn(Optional.of(wallet1));
    when(walletRepository.save(any(Wallet.class))).thenReturn(wallet2);
    assertEquals(wallet2, walletService.updateCurrency(uuid, Currency.UAH));
  }

  @Test
  public void shouldDeleteWallet() {
    CreateWalletDto createWalletDto = new CreateWalletDto(uuid, Currency.USD);
    Wallet wallet = new Wallet(createWalletDto);
    when(walletRepository.findById(uuid)).thenReturn(Optional.of(wallet));
    assertDoesNotThrow(() -> walletService.deleteWallet(uuid));
    verify(walletRepository, times(1)).deleteById(uuid);
  }

  @Test
  public void shouldCompleteDepositTransaction() {
    BigDecimal depositAmount = BigDecimal.valueOf(100);
    Currency currency = Currency.USD;
    CreateWalletDto createWalletDto = new CreateWalletDto(uuid, currency);
    Wallet wallet = new Wallet(createWalletDto);
    Transaction transaction = new Transaction();
    TransactionRequestDto transactionRequestDto = new TransactionRequestDto(depositAmount, currency);
    TransactionResponseDto transactionResponseDto = new TransactionResponseDto(uuid, uuid, depositAmount, currency, TransactionStatus.SUCCESS, LocalDateTime.now());
    when(walletRepository.findById(uuid)).thenReturn(Optional.of(wallet));
    when(walletRepository.save(any(Wallet.class))).thenReturn(wallet);
    when(transactionService.saveTransaction(any(UUID.class), any(TransactionRequestDto.class), any(TransactionStatus.class)))
        .thenReturn(transaction);
    when(transactionMapper.transactionToTransactionResponseDto(any(Transaction.class))).thenReturn(transactionResponseDto);
    TransactionResponseDto response = walletService.transaction(uuid, transactionRequestDto, BigDecimal::add);
    assertNotNull(response);
    assertEquals(TransactionStatus.SUCCESS, response.status());
    verify(walletRepository, times(1)).save(any(Wallet.class));
  }

  @Test
  public void shouldCompleteWithdrawTransaction() {
    BigDecimal withdrawAmount = BigDecimal.valueOf(100);
    Currency currency = Currency.USD;
    CreateWalletDto createWalletDto = new CreateWalletDto(uuid, currency);
    Wallet wallet = new Wallet(createWalletDto);
    wallet.setBalance(BigDecimal.valueOf(200));
    Transaction transaction = new Transaction();
    TransactionRequestDto transactionRequestDto = new TransactionRequestDto(withdrawAmount, currency);
    TransactionResponseDto transactionResponseDto = new TransactionResponseDto(uuid, uuid, withdrawAmount, currency, TransactionStatus.SUCCESS, LocalDateTime.now());
    when(walletRepository.findById(uuid)).thenReturn(Optional.of(wallet));
    when(walletRepository.save(any(Wallet.class))).thenReturn(wallet);
    when(transactionService.saveTransaction(any(UUID.class), any(TransactionRequestDto.class), any(TransactionStatus.class)))
        .thenReturn(transaction);
    when(transactionMapper.transactionToTransactionResponseDto(any(Transaction.class))).thenReturn(transactionResponseDto);
    TransactionResponseDto response = walletService.transaction(uuid, transactionRequestDto, BigDecimal::subtract);
    assertNotNull(response);
    assertEquals(TransactionStatus.SUCCESS, response.status());
    verify(walletRepository, times(1)).save(any(Wallet.class));
  }

  @Test
  public void shouldFailWithdrawTransactionWhenWalletHasNotEnoughBalance() {
    BigDecimal withdrawAmount = BigDecimal.valueOf(100);
    Currency currency = Currency.USD;
    CreateWalletDto createWalletDto = new CreateWalletDto(uuid, currency);
    Wallet wallet = new Wallet(createWalletDto);
    Transaction transaction = new Transaction();
    TransactionRequestDto transactionRequestDto = new TransactionRequestDto(withdrawAmount, currency);
    TransactionResponseDto transactionResponseDto = new TransactionResponseDto(uuid, uuid, withdrawAmount, currency, TransactionStatus.FAILURE, LocalDateTime.now());
    when(walletRepository.findById(uuid)).thenReturn(Optional.of(wallet));
    when(walletRepository.save(any(Wallet.class))).thenReturn(wallet);
    when(transactionService.saveTransaction(any(UUID.class), any(TransactionRequestDto.class), any(TransactionStatus.class)))
        .thenReturn(transaction);
    when(transactionMapper.transactionToTransactionResponseDto(any(Transaction.class))).thenReturn(transactionResponseDto);
    TransactionResponseDto response = walletService.transaction(uuid, transactionRequestDto, BigDecimal::subtract);
    assertNotNull(response);
    assertEquals(TransactionStatus.FAILURE, response.status());
    verify(walletRepository, times(0)).save(any(Wallet.class));
  }

  @Test
  public void shouldSetStatusFailureOnTransactionProcessWhenWalletNotFound() {
    BigDecimal withdrawAmount = BigDecimal.valueOf(100);
    Currency currency = Currency.USD;
    Transaction transaction = new Transaction();
    TransactionRequestDto transactionRequestDto = new TransactionRequestDto(withdrawAmount, currency);
    TransactionResponseDto transactionResponseDto = new TransactionResponseDto(uuid, uuid, withdrawAmount, currency, TransactionStatus.FAILURE, LocalDateTime.now());
    when(walletRepository.findById(uuid)).thenReturn(Optional.empty());
    when(transactionService.saveTransaction(any(UUID.class), any(TransactionRequestDto.class), any(TransactionStatus.class)))
        .thenReturn(transaction);
    when(transactionMapper.transactionToTransactionResponseDto(any(Transaction.class))).thenReturn(transactionResponseDto);
    TransactionResponseDto response = walletService.transaction(uuid, transactionRequestDto, BigDecimal::subtract);
    assertNotNull(response);
    assertEquals(TransactionStatus.FAILURE, response.status());
    verify(walletRepository, times(0)).save(any(Wallet.class));
  }

  @Test
  public void shouldSetStatusFailureOnTransactionProcessWhenWalletCurrencyIsDifferentToTransactionCurrency() {
    BigDecimal withdrawAmount = BigDecimal.valueOf(100);
    Currency walletCurrency = Currency.USD;
    Currency withdrawCurrency = Currency.UAH;
    CreateWalletDto createWalletDto = new CreateWalletDto(uuid, walletCurrency);
    Wallet wallet = new Wallet(createWalletDto);
    wallet.setBalance(BigDecimal.valueOf(200));
    Transaction transaction = new Transaction();
    TransactionRequestDto transactionRequestDto = new TransactionRequestDto(withdrawAmount, withdrawCurrency);
    TransactionResponseDto transactionResponseDto = new TransactionResponseDto(uuid, uuid, withdrawAmount, withdrawCurrency, TransactionStatus.FAILURE, LocalDateTime.now());
    when(walletRepository.findById(uuid)).thenReturn(Optional.of(wallet));
    when(transactionService.saveTransaction(any(UUID.class), any(TransactionRequestDto.class), any(TransactionStatus.class)))
        .thenReturn(transaction);
    when(transactionMapper.transactionToTransactionResponseDto(any(Transaction.class))).thenReturn(transactionResponseDto);
    TransactionResponseDto response = walletService.transaction(uuid, transactionRequestDto, BigDecimal::subtract);
    assertNotNull(response);
    assertEquals(TransactionStatus.FAILURE, response.status());
    verify(walletRepository, times(0)).save(any(Wallet.class));
  }

  @Test
  public void shouldTransferToAnotherWallet() {
    UUID senderWalletId = UUID.randomUUID();
    UUID receivingWalletId = UUID.randomUUID();
    BigDecimal transferAmount = BigDecimal.valueOf(100);
    Currency currency = Currency.USD;
    Wallet senderWallet = new Wallet();
    senderWallet.setWalletId(senderWalletId);
    senderWallet.setBalance(BigDecimal.valueOf(200));
    senderWallet.setCurrency(currency);
    Wallet receivingWallet = new Wallet();
    receivingWallet.setWalletId(receivingWalletId);
    receivingWallet.setBalance(BigDecimal.ZERO);
    receivingWallet.setCurrency(currency);
    Transaction transaction = new Transaction();
    TransferRequestDto transferRequestDto = new TransferRequestDto(receivingWalletId, transferAmount, currency);
    TransferResponseDto transferResponseDto = new TransferResponseDto(uuid, senderWalletId, receivingWalletId, transferAmount, currency, TransactionStatus.SUCCESS, LocalDateTime.now());
    when(walletRepository.findById(senderWalletId)).thenReturn(Optional.of(senderWallet));
    when(walletRepository.findById(receivingWalletId)).thenReturn(Optional.of(receivingWallet));
    when(walletRepository.saveAll(anyList())).thenReturn(List.of(senderWallet, receivingWallet));
    when(transactionService.saveTransaction(any(UUID.class), any(TransferRequestDto.class), any(TransactionStatus.class)))
        .thenReturn(transaction);
    when(transactionMapper.transactionToTransferResponseDto(any(Transaction.class))).thenReturn(transferResponseDto);
    TransferResponseDto response = walletService.transferToAnotherWallet(senderWalletId, transferRequestDto);
    assertNotNull(response);
    assertEquals(TransactionStatus.SUCCESS, response.status());
    verify(walletRepository, times(1)).saveAll(anyList());
  }

  @Test
  public void shouldSetStatusFailureWhenWalletNotFound() {
    UUID senderWalletId = UUID.randomUUID();
    UUID receivingWalletId = UUID.randomUUID();
    BigDecimal transferAmount = BigDecimal.valueOf(100);
    Currency currency = Currency.USD;
    Transaction transaction = new Transaction();
    TransferRequestDto transferRequestDto = new TransferRequestDto(receivingWalletId, transferAmount, currency);
    TransferResponseDto transferResponseDto = new TransferResponseDto(uuid, senderWalletId, receivingWalletId, transferAmount, currency, TransactionStatus.FAILURE, LocalDateTime.now());
    when(walletRepository.findById(senderWalletId)).thenReturn(Optional.empty());
    when(walletRepository.findById(receivingWalletId)).thenReturn(Optional.empty());
    when(transactionService.saveTransaction(any(UUID.class), any(TransferRequestDto.class), any(TransactionStatus.class)))
        .thenReturn(transaction);
    when(transactionMapper.transactionToTransferResponseDto(any(Transaction.class))).thenReturn(transferResponseDto);
    TransferResponseDto response = walletService.transferToAnotherWallet(senderWalletId, transferRequestDto);
    assertEquals(TransactionStatus.FAILURE, response.status());
    verify(walletRepository, times(0)).saveAll(anyList());
  }

  @Test
  public void shouldSetStatusFailureWhenTransferCurrencyNotEqualToWalletCurrency() {
    UUID senderWalletId = UUID.randomUUID();
    UUID receivingWalletId = UUID.randomUUID();
    BigDecimal transferAmount = BigDecimal.valueOf(100);
    Currency transferCurrency = Currency.USD;
    Currency walletCurrency = Currency.UAH;
    Wallet senderWallet = new Wallet();
    senderWallet.setWalletId(senderWalletId);
    senderWallet.setBalance(BigDecimal.valueOf(200));
    senderWallet.setCurrency(walletCurrency);
    Wallet receivingWallet = new Wallet();
    receivingWallet.setWalletId(receivingWalletId);
    receivingWallet.setBalance(BigDecimal.ZERO);
    receivingWallet.setCurrency(walletCurrency);
    Transaction transaction = new Transaction();
    TransferRequestDto transferRequestDto = new TransferRequestDto(receivingWalletId, transferAmount, transferCurrency);
    TransferResponseDto transferResponseDto = new TransferResponseDto(uuid, senderWalletId, receivingWalletId, transferAmount, transferCurrency, TransactionStatus.FAILURE, LocalDateTime.now());
    when(walletRepository.findById(senderWalletId)).thenReturn(Optional.of(senderWallet));
    when(walletRepository.findById(receivingWalletId)).thenReturn(Optional.of(receivingWallet));
    when(transactionService.saveTransaction(any(UUID.class), any(TransferRequestDto.class), any(TransactionStatus.class)))
        .thenReturn(transaction);
    when(transactionMapper.transactionToTransferResponseDto(any(Transaction.class))).thenReturn(transferResponseDto);
    TransferResponseDto response = walletService.transferToAnotherWallet(senderWalletId, transferRequestDto);
    assertEquals(TransactionStatus.FAILURE, response.status());
    verify(walletRepository, times(0)).saveAll(anyList());
  }

  @Test
  public void shouldSetStatusFailureWhenCurrencyNotEqualBetweenWallets() {
    UUID senderWalletId = UUID.randomUUID();
    UUID receivingWalletId = UUID.randomUUID();
    BigDecimal transferAmount = BigDecimal.valueOf(100);
    Currency senderCurrency = Currency.USD;
    Currency receivingCurrency = Currency.UAH;
    Wallet senderWallet = new Wallet();
    senderWallet.setWalletId(senderWalletId);
    senderWallet.setBalance(BigDecimal.valueOf(200));
    senderWallet.setCurrency(senderCurrency);
    Wallet receivingWallet = new Wallet();
    receivingWallet.setWalletId(receivingWalletId);
    receivingWallet.setBalance(BigDecimal.ZERO);
    receivingWallet.setCurrency(receivingCurrency);
    Transaction transaction = new Transaction();
    TransferRequestDto transferRequestDto = new TransferRequestDto(receivingWalletId, transferAmount, senderCurrency);
    TransferResponseDto transferResponseDto = new TransferResponseDto(uuid, senderWalletId, receivingWalletId, transferAmount, senderCurrency, TransactionStatus.FAILURE, LocalDateTime.now());
    when(walletRepository.findById(senderWalletId)).thenReturn(Optional.of(senderWallet));
    when(walletRepository.findById(receivingWalletId)).thenReturn(Optional.of(receivingWallet));
    when(transactionService.saveTransaction(any(UUID.class), any(TransferRequestDto.class), any(TransactionStatus.class)))
        .thenReturn(transaction);
    when(transactionMapper.transactionToTransferResponseDto(any(Transaction.class))).thenReturn(transferResponseDto);
    TransferResponseDto response = walletService.transferToAnotherWallet(senderWalletId, transferRequestDto);
    assertEquals(TransactionStatus.FAILURE, response.status());
    verify(walletRepository, times(0)).saveAll(anyList());
  }

  @Test
  public void shouldSetStatusFailureWhenSenderWalletHasNotEnoughMoney() {
    UUID senderWalletId = UUID.randomUUID();
    UUID receivingWalletId = UUID.randomUUID();
    BigDecimal transferAmount = BigDecimal.valueOf(100);
    Currency currency = Currency.USD;
    Wallet senderWallet = new Wallet();
    senderWallet.setWalletId(senderWalletId);
    senderWallet.setBalance(BigDecimal.ZERO);
    senderWallet.setCurrency(currency);
    Wallet receivingWallet = new Wallet();
    receivingWallet.setWalletId(receivingWalletId);
    receivingWallet.setBalance(BigDecimal.ZERO);
    receivingWallet.setCurrency(currency);
    Transaction transaction = new Transaction();
    TransferRequestDto transferRequestDto = new TransferRequestDto(receivingWalletId, transferAmount, currency);
    TransferResponseDto transferResponseDto = new TransferResponseDto(uuid, senderWalletId, receivingWalletId, transferAmount, currency, TransactionStatus.FAILURE, LocalDateTime.now());
    when(walletRepository.findById(senderWalletId)).thenReturn(Optional.of(senderWallet));
    when(walletRepository.findById(receivingWalletId)).thenReturn(Optional.of(receivingWallet));
    when(walletRepository.saveAll(anyList())).thenReturn(List.of(senderWallet, receivingWallet));
    when(transactionService.saveTransaction(any(UUID.class), any(TransferRequestDto.class), any(TransactionStatus.class)))
        .thenReturn(transaction);
    when(transactionMapper.transactionToTransferResponseDto(any(Transaction.class))).thenReturn(transferResponseDto);
    TransferResponseDto response = walletService.transferToAnotherWallet(senderWalletId, transferRequestDto);
    assertEquals(TransactionStatus.FAILURE, response.status());
    verify(walletRepository, times(0)).saveAll(anyList());
  }

  @Test
  public void shouldGetWalletByAccountIdAndCurrency() {
    Wallet wallet = new Wallet(new CreateWalletDto(uuid, Currency.USD));
    when(walletRepository.findByAccountIdAndCurrency(uuid, Currency.USD)).thenReturn(Optional.of(wallet));
    assertDoesNotThrow(() -> walletService.getWalletByAccountIdAndCurrency(uuid, Currency.USD));
  }

  @Test
  public void shouldThrowExceptionWhenThereIsNoWalletByAccountIdAndCurrency() {
    when(walletRepository.findByAccountIdAndCurrency(uuid, Currency.USD)).thenReturn(Optional.empty());
    assertThrows(EntityNotFoundException.class, () -> walletService.getWalletByAccountIdAndCurrency(uuid, Currency.USD));
  }
}
