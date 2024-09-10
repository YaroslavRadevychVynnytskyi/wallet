package com.nerdysoft.walletservice.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nerdysoft.walletservice.dto.request.TransactionRequestDto;
import com.nerdysoft.walletservice.dto.request.TransferRequestDto;
import com.nerdysoft.walletservice.model.Transaction;
import com.nerdysoft.walletservice.model.enums.Currency;
import com.nerdysoft.walletservice.model.enums.TransactionStatus;
import com.nerdysoft.walletservice.repository.TransactionRepository;
import java.math.BigDecimal;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class TransactionServiceTest {
  @Mock
  private TransactionRepository transactionRepository;

  @InjectMocks
  private TransactionService transactionService;

  private final UUID uuid = UUID.randomUUID();

  private final TransactionStatus transactionStatus = TransactionStatus.SUCCESS;

  private AutoCloseable mocks;

  @BeforeEach
  void setUp() {
    mocks = MockitoAnnotations.openMocks(this);
  }

  @AfterEach
  void tearDown() {
    try {
      mocks.close();
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

  @Test
  public void shouldSaveTransactionRequest() {
    TransactionRequestDto transactionRequestDto = new TransactionRequestDto(BigDecimal.valueOf(100), Currency.USD);
    Transaction transaction = new Transaction(uuid, transactionRequestDto.amount(), transactionRequestDto.currency(), transactionStatus);
    when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);
    assertNotNull(transactionService.saveTransaction(uuid, transactionRequestDto, transactionStatus));
    verify(transactionRepository, times(1)).save(any(Transaction.class));
  }

  @Test
  public void shouldSaveTransferRequest() {
    TransferRequestDto transferRequestDto = new TransferRequestDto(uuid, BigDecimal.valueOf(100), Currency.USD);
    Transaction transaction = new Transaction(uuid, transferRequestDto.amount(), transferRequestDto.currency(), transactionStatus, transferRequestDto.toWalletId());
    when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);
    assertNotNull(transactionService.saveTransaction(uuid, transferRequestDto, transactionStatus));
    verify(transactionRepository, times(1)).save(any(Transaction.class));
  }
}
