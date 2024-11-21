package com.nerdysoft.service;

import com.nerdysoft.axon.command.transaction.CreateTransactionCommand;
import com.nerdysoft.axon.command.wallet.CancelWithdrawFromWalletCommand;
import com.nerdysoft.axon.query.loanlimit.FindLoanLimitByWalletIdQuery;
import com.nerdysoft.axon.query.transaction.FindTransactionByIdQuery;
import com.nerdysoft.dto.feign.ConvertAmountRequestDto;
import com.nerdysoft.dto.loanlimit.LoanLimitDto;
import com.nerdysoft.dto.request.CreateWalletDto;
import com.nerdysoft.dto.request.DepositRequestDto;
import com.nerdysoft.dto.request.TransactionRequestDto;
import com.nerdysoft.dto.request.TransferRequestDto;
import com.nerdysoft.dto.request.WalletOperationRequestDto;
import com.nerdysoft.dto.request.WithdrawRequestDto;
import com.nerdysoft.dto.response.ConvertAmountResponseDto;
import com.nerdysoft.dto.response.DepositResponseDto;
import com.nerdysoft.dto.response.TransferResponseDto;
import com.nerdysoft.dto.response.WithdrawResponseDto;
import com.nerdysoft.entity.Transaction;
import com.nerdysoft.entity.Wallet;
import com.nerdysoft.feign.CommissionFeignClient;
import com.nerdysoft.feign.CurrencyExchangeFeignClient;
import com.nerdysoft.feign.LoanLimitFeignClient;
import com.nerdysoft.mapper.TransactionMapper;
import com.nerdysoft.mapper.WalletMapper;
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
import lombok.RequiredArgsConstructor;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WalletService {
  private final WalletRepository walletRepository;

  private final WalletMapper walletMapper;

  private final CommandGateway commandGateway;

  private final QueryGateway queryGateway;

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
      return walletRepository.save(walletMapper.toWallet(dto));
    }
  }

  public Wallet findById(UUID walletId) {
    return walletRepository.findById(walletId).orElseThrow(
        () -> new EntityNotFoundException(String.format("No wallets with id: %s", walletId)));
  }

  public Wallet findWalletByAccountIdAndCurrency(UUID accountId, Currency currency) {
    return walletRepository.findByAccountIdAndCurrency(accountId, currency)
        .orElseThrow(() -> new EntityNotFoundException(
            String.format("No wallets for account: %s and with currency: %s", accountId, currency)));
  }

  public Wallet updateCurrency(UUID walletId, Currency currency) {
    Wallet wallet = findById(walletId);

    BigDecimal convertedAmount = convertCurrency(new ConvertAmountRequestDto(wallet.getCurrency().getCode(),
        currency.getCode(), wallet.getBalance()));

    wallet.setCurrency(currency);
    wallet.setBalance(convertedAmount);

    return walletRepository.save(wallet);
  }

  public String deleteById(UUID walletId) {
    findById(walletId);
    walletRepository.deleteById(walletId);
    return String.format("Wallet with id %s was deleted", walletId);
  }

  @Transactional
  public DepositResponseDto deposit(UUID walletId, DepositRequestDto depositRequestDto) {
    Transaction transaction = transaction(
        walletId,
        depositRequestDto,
        BigDecimal::add
    );

    return transactionMapper.toDepositResponseDto(transaction);
  }

  @Transactional
  public WithdrawResponseDto withdraw(UUID walletId, WithdrawRequestDto withdrawRequestDto) {
    Transaction transaction = transaction(
        walletId,
        withdrawRequestDto,
        BigDecimal::subtract
    );

    return transactionMapper.toWithdrawResponseDto(transaction);
  }

  private Transaction transaction(
      UUID walletId,
      TransactionRequestDto transactionRequestDto,
      BiFunction<BigDecimal, BigDecimal, BigDecimal> operation
  ) {
    Wallet wallet = findById(walletId);

    BigDecimal transactionAmount = transactionRequestDto.getAmount();

    boolean isUsedLoanLimit = false;
    BigDecimal usedLoanLimitAmount = BigDecimal.ZERO;

    if (!wallet.getCurrency().equals(transactionRequestDto.getCurrency())) {
      transactionAmount = convertCurrency(new ConvertAmountRequestDto(
          transactionRequestDto.getCurrency().getCode(),
          wallet.getCurrency().getCode(), transactionRequestDto.getAmount()));
    }

    wallet.setBalance(operation.apply(wallet.getBalance(), transactionAmount));

    if (wallet.getBalance().compareTo(BigDecimal.ZERO) < 0) {
      isUsedLoanLimit = true;
      usedLoanLimitAmount = wallet.getBalance().negate();

      BigDecimal loanLimitAmount = queryGateway.query(new FindLoanLimitByWalletIdQuery(walletId), LoanLimitDto.class)
          .thenApply(LoanLimitDto::getAvailableAmount)
          .exceptionally(e -> BigDecimal.ZERO)
          .join();

      if (loanLimitAmount.subtract(usedLoanLimitAmount).compareTo(BigDecimal.ZERO) < 0) {
        return saveTransaction(wallet, transactionRequestDto, true, usedLoanLimitAmount,
            TransactionStatus.FAILURE);
      }

      wallet.setBalance(BigDecimal.ZERO);
    }

    walletRepository.save(wallet);

    if (transactionRequestDto instanceof DepositRequestDto) {
      return saveTransaction(wallet, transactionRequestDto, isUsedLoanLimit, usedLoanLimitAmount,
          TransactionStatus.SUCCESS);
    } else {
      return saveTransaction(wallet, transactionRequestDto, isUsedLoanLimit, usedLoanLimitAmount,
          TransactionStatus.PENDING);
    }
  }

  public BigDecimal cancelWithdraw(CancelWithdrawFromWalletCommand command) {
    Wallet wallet = findById(command.getWalletId());

    BigDecimal usedAmountWithoutLoanLimit = command.getAmount().subtract(command.getUsedLoanLimitAvailableAmount());

    wallet.setBalance(wallet.getBalance().add(usedAmountWithoutLoanLimit));

    walletRepository.save(wallet);

    return wallet.getBalance();
  }

  @Transactional
  public TransferResponseDto transferToAnotherWallet(UUID walletId,
      TransferRequestDto transferRequestDto) {
//    Wallet senderWallet = findById(walletId);
//    Wallet receiverWallet = findById(transferRequestDto.getToWalletId());
//
//    BigDecimal transferAmount = validateAndConvertCurrency(senderWallet, transferRequestDto);
//    BigDecimal senderBalance = senderWallet.getBalance();
//
//    BigDecimal loanLimitAmount = BigDecimal.valueOf(0);
//    boolean isBalanceInsufficient = senderBalance.compareTo(transferAmount) < 0;
//
//    if (isBalanceInsufficient) {
//      loanLimitAmount = applyLoanLimitIfNeeded(senderWallet, transferAmount);
//      senderBalance = senderBalance.add(loanLimitAmount);
//    }
//    if (senderBalance.compareTo(transferAmount) < 0) {
//      return saveTransaction(senderWallet, transferRequestDto, TransactionStatus.FAILURE,
//          senderWallet.getBalance(), transactionMapper::transactionToTransferResponseDto);
//    }
//
//    CalcCommissionResponseDto commission = commissionFeignClient.calculateCommission(
//        CalcCommissionRequestDto.builder()
//            .walletAmount(transferAmount.subtract(loanLimitAmount))
//            .isLoanLimitUsed(isBalanceInsufficient)
//            .loanLimitAmount(loanLimitAmount)
//            .fromWalletCurrency(senderWallet.getCurrency().getCode())
//            .toWalletCurrency(receiverWallet.getCurrency().getCode())
//            .transactionCurrency(transferRequestDto.getCurrency().getCode())
//            .build()
//    ).getBody();
//
//    updateWalletBalances(senderWallet, receiverWallet, transferAmount, senderBalance,
//        commission.getOriginalCurrencyCommission());
//    TransferResponseDto transferResponseDto = saveTransaction(senderWallet, transferRequestDto,
//        TransactionStatus.SUCCESS,
//        senderBalance, transactionMapper::transactionToTransferResponseDto);
//
//    commissionFeignClient.saveCommission(
//        new SaveCommissionRequestDto(transferResponseDto.getTransactionId(), commission));
//
//    return transferResponseDto;

    return null;
  }

  private BigDecimal applyLoanLimitIfNeeded(Wallet wallet, BigDecimal transferAmount) {
    LoanLimitDto loanLimitDto = loanLimitFeignClient.getLoanLimitByWalletId(wallet.getWalletId())
        .getBody();
    BigDecimal availableFunds = wallet.getBalance().add(loanLimitDto.getAvailableAmount());

    if (availableFunds.compareTo(transferAmount) >= 0) {
      BigDecimal requiredLoanAmount = transferAmount.subtract(wallet.getBalance());
      loanLimitDto.setAvailableAmount(loanLimitDto.getAvailableAmount().subtract(requiredLoanAmount));
      loanLimitFeignClient.updateByWalletId(wallet.getWalletId(), loanLimitDto);

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

  private Transaction saveTransaction(Wallet wallet, WalletOperationRequestDto requestDto,
      boolean isUsedLoanLimit, BigDecimal usedLoanLimitAmount, TransactionStatus status) {
    CreateTransactionCommand.CreateTransactionCommandBuilder builder = CreateTransactionCommand.builder()
        .walletId(wallet.getWalletId())
        .walletBalance(wallet.getBalance())
        .amount(requestDto.getAmount())
        .usedLoanLimit(isUsedLoanLimit)
        .usedLoanLimitAmount(usedLoanLimitAmount)
        .operationCurrency(requestDto.getCurrency())
        .walletCurrency(wallet.getCurrency())
        .status(status);

    if (requestDto instanceof TransferRequestDto) {
      builder.toWalletId(((TransferRequestDto) requestDto).getToWalletId());
    }

    UUID transactionId = commandGateway.sendAndWait(builder.build());

    return queryGateway.query(new FindTransactionByIdQuery(transactionId), Transaction.class).join();
  }

  private BigDecimal convertCurrency(ConvertAmountRequestDto requestDto) {
    Optional<ConvertAmountResponseDto> convertDto = Optional.ofNullable(
        currencyExchangeFeignClient.convert(requestDto).getBody());
    if (convertDto.isPresent()) {
      return convertDto.get().convertedAmount();
    } else {
      throw new UniqueException("Failed to convert", HttpStatus.BAD_REQUEST);
    }
  }
}
