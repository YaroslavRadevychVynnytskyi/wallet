package com.nerdysoft.walletservice.service;

import com.nerdysoft.walletservice.dto.feign.LoanLimit;
import com.nerdysoft.walletservice.dto.rabbit.CommissionRequestMessage;
import com.nerdysoft.walletservice.dto.request.ConvertAmountRequestDto;
import com.nerdysoft.walletservice.dto.request.CreateWalletDto;
import com.nerdysoft.walletservice.dto.request.TransactionRequestDto;
import com.nerdysoft.walletservice.dto.request.TransferRequestDto;
import com.nerdysoft.walletservice.dto.request.TransferTransactionRequestDto;
import com.nerdysoft.walletservice.dto.request.TransferTransactionResponseDto;
import com.nerdysoft.walletservice.dto.response.TransactionResponseDto;
import com.nerdysoft.walletservice.dto.response.TransferResponseDto;
import com.nerdysoft.walletservice.exception.UniqueException;
import com.nerdysoft.walletservice.feign.CurrencyExchangeFeignClient;
import com.nerdysoft.walletservice.feign.LoanLimitFeignClient;
import com.nerdysoft.walletservice.mapper.TransactionMapper;
import com.nerdysoft.walletservice.model.Transaction;
import com.nerdysoft.walletservice.model.Wallet;
import com.nerdysoft.walletservice.model.enums.Currency;
import com.nerdysoft.walletservice.model.enums.TransactionStatus;
import com.nerdysoft.walletservice.repository.WalletRepository;
import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WalletService {
  private final WalletRepository walletRepository;

  private final TransactionService transactionService;

  private final TransactionMapper transactionMapper;

  private final CurrencyExchangeFeignClient currencyExchangeFeignClient;

  private final LoanLimitFeignClient loanLimitFeignClient;

  private final CommissionMessageProducer commissionMessageProducer;

  public Wallet createWallet(CreateWalletDto createWalletDto) {
    if (walletRepository.hasAccountWalletOnThisCurrency(createWalletDto.accountId(),
        createWalletDto.currency())) {
      throw new UniqueException(String.format("This account has already wallet on %s currency",
          createWalletDto.currency()), HttpStatus.NOT_ACCEPTABLE);
    } else {
      Wallet wallet = new Wallet(createWalletDto);
      return walletRepository.save(wallet);
    }
  }

  public Wallet getWallet(UUID walletId) {
    return walletRepository.findById(walletId).orElseThrow(EntityNotFoundException::new);
  }

  public Wallet updateCurrency(UUID walletId, Currency currency) {
    Wallet wallet = getWallet(walletId);
    wallet.setCurrency(currency);
    return walletRepository.save(wallet);
  }

  public String deleteWallet(UUID walletId) {
    getWallet(walletId);
    walletRepository.deleteById(walletId);
    return String.format("Wallet with id %s was deleted", walletId);
  }

  public Wallet getWalletByAccountIdAndCurrency(UUID accountId, Currency currency) {
    return walletRepository.findByAccountIdAndCurrency(accountId, currency)
            .orElseThrow(EntityNotFoundException::new);
  }

  @Transactional
  public TransactionResponseDto transaction(UUID walletId,
                                            TransactionRequestDto transactionRequestDto,
                                            BiFunction<BigDecimal, BigDecimal, BigDecimal> operation) {
    Wallet wallet = getWalletById(walletId);

    BigDecimal transactionAmount = validateAndConvertCurrency(wallet, transactionRequestDto);
    BigDecimal walletBalance = wallet.getBalance();

    BigDecimal resultBalance = operation.apply(walletBalance, transactionAmount);
    if (resultBalance.compareTo(walletBalance) < 0 && walletBalance.compareTo(transactionAmount) < 0) {
      BigDecimal loanLimitAmount = applyLoanLimitIfNeeded(wallet, transactionAmount);
      walletBalance = walletBalance.add(loanLimitAmount);
    }

    resultBalance = operation.apply(walletBalance, transactionAmount);

    if (resultBalance.compareTo(BigDecimal.ZERO) < 0) {
      return saveTransaction(wallet, transactionRequestDto, TransactionStatus.FAILURE,
              wallet.getBalance(), transactionMapper::transactionToTransactionResponseDto);
    }

    wallet.setBalance(resultBalance);
    walletRepository.save(wallet);

    return saveTransaction(wallet, transactionRequestDto, TransactionStatus.SUCCESS,
            resultBalance, transactionMapper::transactionToTransactionResponseDto);
  }

  @Transactional
  public TransferResponseDto transferToAnotherWallet(UUID walletId,
                                                     TransferRequestDto transferRequestDto) {
    Wallet senderWallet = getWalletById(walletId);
    Wallet receiverWallet = getWalletById(transferRequestDto.toWalletId());

    BigDecimal transferAmount = validateAndConvertCurrency(senderWallet, transferRequestDto);
    BigDecimal senderBalance = senderWallet.getBalance();

    BigDecimal loanLimitAmount = BigDecimal.valueOf(0);
    boolean isBalanceInsufficient = senderBalance.compareTo(transferAmount) < 0;

    if (isBalanceInsufficient) {
      loanLimitAmount = applyLoanLimitIfNeeded(senderWallet, transferAmount);
      senderBalance = senderBalance.add(loanLimitAmount);
    }

    if (senderBalance.compareTo(transferAmount) < 0) {
      return saveTransaction(senderWallet, transferRequestDto, TransactionStatus.FAILURE,
              senderWallet.getBalance(), transactionMapper::transactionToTransferResponseDto);
    }

    updateWalletBalances(senderWallet, receiverWallet, transferAmount, senderBalance);
    TransferResponseDto transferResponseDto = saveTransaction(senderWallet, transferRequestDto, TransactionStatus.SUCCESS,
            senderBalance, transactionMapper::transactionToTransferResponseDto);

    commissionMessageProducer.sendMessage(CommissionRequestMessage.builder()
            .transactionId(transferResponseDto.transactionId())
            .walletAmount(transferAmount.subtract(loanLimitAmount))
            .isLoanLimitUsed(isBalanceInsufficient)
            .loanLimitAmount(loanLimitAmount)
            .fromWalletCurrency(senderWallet.getCurrency().getCode())
            .toWalletCurrency(receiverWallet.getCurrency().getCode())
            .transactionCurrency(transferRequestDto.currency().getCode())
            .build()
    );

    return transferResponseDto;
  }

  private Wallet getWalletById(UUID walletId) {
    return walletRepository.findById(walletId).orElseThrow(() ->
            new EntityNotFoundException("Can't find wallet with ID: " + walletId));
  }

  private BigDecimal validateAndConvertCurrency(Wallet senderWallet, TransferTransactionRequestDto requestDto) {
    if (!senderWallet.getCurrency().equals(requestDto.getCurrency())) {
      return currencyExchangeFeignClient.convert(new ConvertAmountRequestDto(
              requestDto.getCurrency().getCode(),
              senderWallet.getCurrency().getCode(),
              requestDto.getAmount()
      )).getBody().convertedAmount();
    }

    return requestDto.getAmount();
  }

  private BigDecimal applyLoanLimitIfNeeded(Wallet wallet, BigDecimal transferAmount) {
    LoanLimit loanLimit = loanLimitFeignClient.getLoanLimitByWalletId(wallet.getWalletId()).getBody();
    BigDecimal availableFunds = wallet.getBalance().add(loanLimit.getAvailableLoanLimit());

    if (availableFunds.compareTo(transferAmount) >= 0) {
      BigDecimal requiredLoanAmount = transferAmount.subtract(wallet.getBalance());
      loanLimit.setAvailableLoanLimit(loanLimit.getAvailableLoanLimit().subtract(requiredLoanAmount));
      loanLimitFeignClient.updateByWalletId(wallet.getWalletId(), loanLimit);

      return requiredLoanAmount;
    }

    return BigDecimal.ZERO;
  }

  private void updateWalletBalances(Wallet senderWallet, Wallet receiverWallet, BigDecimal transferAmount, BigDecimal senderBalance) {
    BigDecimal senderNewBalance = senderBalance.subtract(transferAmount);
    senderWallet.setBalance(senderNewBalance);

    BigDecimal receiverTransferAmount;
    if (!senderWallet.getCurrency().equals(receiverWallet.getCurrency())) {
      receiverTransferAmount = currencyExchangeFeignClient.convert(new ConvertAmountRequestDto(
              senderWallet.getCurrency().getCode(),
              receiverWallet.getCurrency().getCode(),
              transferAmount
      )).getBody().convertedAmount();
    } else {
      receiverTransferAmount = transferAmount;
    }

    BigDecimal receiverNewBalance = receiverWallet.getBalance().add(receiverTransferAmount);
    receiverWallet.setBalance(receiverNewBalance);

    walletRepository.saveAll(List.of(senderWallet, receiverWallet));
  }

  private <T extends TransferTransactionResponseDto> T saveTransaction(Wallet wallet,
                                                                       TransferTransactionRequestDto requestDto,
                                                                       TransactionStatus status,
                                                                       BigDecimal balance,
                                                                       Function<Transaction, T> mapper) {
    Transaction transaction = transactionService.saveTransaction(
            wallet.getWalletId(),
            requestDto,
            status,
            balance
    );

    return mapper.apply(transaction);
  }
}
