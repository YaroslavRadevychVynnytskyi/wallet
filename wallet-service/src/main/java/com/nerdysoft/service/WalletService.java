package com.nerdysoft.service;

import com.nerdysoft.dto.feign.CalcCommissionRequestDto;
import com.nerdysoft.dto.feign.CalcCommissionResponseDto;
import com.nerdysoft.dto.feign.LoanLimit;
import com.nerdysoft.dto.feign.SaveCommissionRequestDto;
import com.nerdysoft.dto.request.ConvertAmountRequestDto;
import com.nerdysoft.dto.request.CreateWalletDto;
import com.nerdysoft.dto.request.TransactionRequestDto;
import com.nerdysoft.dto.request.TransferRequestDto;
import com.nerdysoft.dto.request.WalletOperationRequestDto;
import com.nerdysoft.dto.request.WalletOperationResponseDto;
import com.nerdysoft.dto.response.ConvertAmountResponseDto;
import com.nerdysoft.dto.response.TransactionResponseDto;
import com.nerdysoft.dto.response.TransferResponseDto;
import com.nerdysoft.feign.CommissionFeignClient;
import com.nerdysoft.feign.CurrencyExchangeFeignClient;
import com.nerdysoft.feign.LoanLimitFeignClient;
import com.nerdysoft.mapper.TransactionMapper;
import com.nerdysoft.model.Transaction;
import com.nerdysoft.model.Wallet;
import com.nerdysoft.model.enums.Currency;
import com.nerdysoft.model.enums.TransactionStatus;
import com.nerdysoft.model.exception.UniqueException;
import com.nerdysoft.repository.WalletRepository;
import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
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

  private final CommissionFeignClient commissionFeignClient;

  public Wallet createWallet(CreateWalletDto dto) {
    if (walletRepository.hasAccountWalletOnThisCurrency(dto.accountId(),
        dto.currency())) {
      throw new UniqueException(String.format("This account has already wallet on %s currency",
          dto.currency()), HttpStatus.NOT_ACCEPTABLE);
    } else {
      return walletRepository.save(new Wallet(dto));
    }
  }

  public Wallet findById(UUID walletId) {
    return walletRepository.findById(walletId).orElseThrow(
        () -> new EntityNotFoundException(String.format("No wallets with id: %s", walletId)));
  }

  public Wallet updateCurrency(UUID walletId, Currency currency) {
    Wallet wallet = findById(walletId);
    Optional<ConvertAmountResponseDto> convertDto = Optional.ofNullable(
        currencyExchangeFeignClient.convert(
            new ConvertAmountRequestDto(wallet.getCurrency().getCode(), currency.getCode(),
                wallet.getBalance())
        ).getBody());
    if (convertDto.isPresent()) {
      wallet.setCurrency(currency);
      wallet.setBalance(convertDto.get().convertedAmount());
      return walletRepository.save(wallet);
    } else {
      throw new UniqueException("Failed to convert amount", HttpStatus.NOT_ACCEPTABLE);
    }
  }

  public String deleteById(UUID walletId) {
    findById(walletId);
    walletRepository.deleteById(walletId);
    return String.format("Wallet with id %s was deleted", walletId);
  }

  public Wallet findWalletByAccountIdAndCurrency(UUID accountId, Currency currency) {
    return walletRepository.findByAccountIdAndCurrency(accountId, currency)
        .orElseThrow(() -> new EntityNotFoundException(
            String.format("No wallets for account: %s and with currency: %s", accountId,
                currency)));
  }

  @Transactional
  public TransactionResponseDto transaction(UUID walletId,
      TransactionRequestDto transactionRequestDto,
      BiFunction<BigDecimal, BigDecimal, BigDecimal> operation) {
    Wallet wallet = findById(walletId);

    BigDecimal transactionAmount = validateAndConvertCurrency(wallet, transactionRequestDto);
    BigDecimal walletBalance = wallet.getBalance();

    BigDecimal resultBalance = operation.apply(walletBalance, transactionAmount);
    if (resultBalance.compareTo(walletBalance) < 0
        && walletBalance.compareTo(transactionAmount) < 0) {
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
    Wallet senderWallet = findById(walletId);
    Wallet receiverWallet = findById(transferRequestDto.toWalletId());

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

    CalcCommissionResponseDto commission = commissionFeignClient.calculateCommission(
        CalcCommissionRequestDto.builder()
            .walletAmount(transferAmount.subtract(loanLimitAmount))
            .isLoanLimitUsed(isBalanceInsufficient)
            .loanLimitAmount(loanLimitAmount)
            .fromWalletCurrency(senderWallet.getCurrency().getCode())
            .toWalletCurrency(receiverWallet.getCurrency().getCode())
            .transactionCurrency(transferRequestDto.currency().getCode())
            .build()
    ).getBody();

    updateWalletBalances(senderWallet, receiverWallet, transferAmount, senderBalance,
        commission.getOriginalCurrencyCommission());
    TransferResponseDto transferResponseDto = saveTransaction(senderWallet, transferRequestDto,
        TransactionStatus.SUCCESS,
        senderBalance, transactionMapper::transactionToTransferResponseDto);

    commissionFeignClient.saveCommission(
        new SaveCommissionRequestDto(transferResponseDto.transactionId(), commission));

    return transferResponseDto;
  }

  private BigDecimal validateAndConvertCurrency(Wallet senderWallet,
      WalletOperationRequestDto requestDto) {
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
    LoanLimit loanLimit = loanLimitFeignClient.getLoanLimitByWalletId(wallet.getWalletId())
        .getBody();
    BigDecimal availableFunds = wallet.getBalance().add(loanLimit.getAvailableAmount());

    if (availableFunds.compareTo(transferAmount) >= 0) {
      BigDecimal requiredLoanAmount = transferAmount.subtract(wallet.getBalance());
      loanLimit.setAvailableAmount(loanLimit.getAvailableAmount().subtract(requiredLoanAmount));
      loanLimitFeignClient.updateByWalletId(wallet.getWalletId(), loanLimit);

      return requiredLoanAmount;
    }

    return BigDecimal.ZERO;
  }

  private void updateWalletBalances(Wallet senderWallet, Wallet receiverWallet,
      BigDecimal transferAmount, BigDecimal senderBalance, BigDecimal commission) {
    BigDecimal senderNewBalance = senderBalance.subtract(transferAmount).subtract(commission);

    senderNewBalance = checkIfBalanceEnoughForCommission(senderWallet, senderNewBalance,
        commission);
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

  private BigDecimal checkIfBalanceEnoughForCommission(Wallet senderWallet,
      BigDecimal senderNewBalance, BigDecimal commission) {
    if (senderNewBalance.compareTo(BigDecimal.ZERO) < 0) {
      senderWallet.setBalance(BigDecimal.ZERO);

      BigDecimal loanLimit = applyLoanLimitIfNeeded(senderWallet, commission);
      senderNewBalance = senderNewBalance.add(loanLimit);
    }

    if (senderNewBalance.compareTo(BigDecimal.ZERO) < 0) {
      throw new IllegalStateException("Insufficient funds including loan limit");
    }

    return senderNewBalance;
  }

  private <T extends WalletOperationResponseDto> T saveTransaction(Wallet wallet,
      WalletOperationRequestDto requestDto,
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
