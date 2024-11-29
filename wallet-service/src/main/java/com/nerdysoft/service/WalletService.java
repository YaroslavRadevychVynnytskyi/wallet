package com.nerdysoft.service;

import com.nerdysoft.axon.command.transaction.CreateTransactionCommand;
import com.nerdysoft.axon.command.wallet.CancelWithdrawFromWalletCommand;
import com.nerdysoft.axon.query.commission.CalculateCommissionQuery;
import com.nerdysoft.axon.query.transaction.FindTransactionByIdQuery;
import com.nerdysoft.dto.commission.CalcCommissionResponseDto;
import com.nerdysoft.dto.feign.ConvertAmountRequestDto;
import com.nerdysoft.dto.feign.ConvertAmountResponseDto;
import com.nerdysoft.dto.request.CreateWalletDto;
import com.nerdysoft.dto.request.DepositRequestDto;
import com.nerdysoft.dto.request.TransactionRequestDto;
import com.nerdysoft.dto.request.TransferRequestDto;
import com.nerdysoft.dto.request.WalletOperationRequestDto;
import com.nerdysoft.dto.request.WithdrawRequestDto;
import com.nerdysoft.dto.response.DepositResponseDto;
import com.nerdysoft.dto.response.TransferResponseDto;
import com.nerdysoft.dto.response.WithdrawResponseDto;
import com.nerdysoft.entity.Transaction;
import com.nerdysoft.entity.Wallet;
import com.nerdysoft.feign.CurrencyExchangeFeignClient;
import com.nerdysoft.mapper.TransactionMapper;
import com.nerdysoft.mapper.WalletMapper;
import com.nerdysoft.model.enums.Currency;
import com.nerdysoft.model.enums.TransactionStatus;
import com.nerdysoft.model.exception.UniqueException;
import com.nerdysoft.repository.WalletRepository;
import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
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

  private final TransactionMapper transactionMapper;

  private final CommandGateway commandGateway;

  private final QueryGateway queryGateway;

  private final CurrencyExchangeFeignClient currencyExchangeFeignClient;

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
            String.format("No wallets for account: %s and with currency: %s", accountId,
                currency)));
  }

  public Wallet updateCurrency(UUID walletId, Currency currency) {
    Wallet wallet = findById(walletId);

    BigDecimal convertedAmount = convertCurrency(
        new ConvertAmountRequestDto(wallet.getCurrency().getCode(),
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

      if (wallet.getCurrency().equals(Currency.USD)) {
        usedLoanLimitAmount = wallet.getBalance().negate();
      } else {
        usedLoanLimitAmount = convertCurrency(
            new ConvertAmountRequestDto(wallet.getCurrency().getCode(), Currency.USD.getCode(),
                wallet.getBalance().negate())
        );
      }

      wallet.setBalance(BigDecimal.ZERO);
    }

    walletRepository.save(wallet);

    if (transactionRequestDto instanceof DepositRequestDto) {
      return saveTransaction(wallet, transactionRequestDto, isUsedLoanLimit, usedLoanLimitAmount,
          null, TransactionStatus.SUCCESS);
    } else {
      return saveTransaction(wallet, transactionRequestDto, isUsedLoanLimit, usedLoanLimitAmount,
          null, TransactionStatus.PENDING);
    }
  }

  public BigDecimal cancelWithdraw(CancelWithdrawFromWalletCommand command) {
    Wallet wallet = findById(command.getWalletId());

    BigDecimal usedAmountWithoutLoanLimit = command.getAmount()
        .subtract(command.getUsedLoanLimitAvailableAmount());

    wallet.setBalance(wallet.getBalance().add(usedAmountWithoutLoanLimit));

    walletRepository.save(wallet);

    return wallet.getBalance();
  }

  @Transactional
  public TransferResponseDto transferToAnotherWallet(UUID walletId, TransferRequestDto transferRequestDto) {
    Wallet senderWallet = findById(walletId);
    Wallet receiverWallet = findById(transferRequestDto.getToWalletId());

    BigDecimal transferAmount = transferRequestDto.getAmount();

    if (!senderWallet.getCurrency().equals(transferRequestDto.getCurrency())) {
      transferAmount = convertCurrency(
          new ConvertAmountRequestDto(transferRequestDto.getCurrency().getCode(),
              senderWallet.getCurrency().getCode(), transferAmount));
    }

    senderWallet.setBalance(senderWallet.getBalance().subtract(transferAmount));

    boolean isUsedLoanLimit = false;
    BigDecimal usedLoanLimitAmount = BigDecimal.ZERO;

    if (senderWallet.getBalance().compareTo(BigDecimal.ZERO) < 0) {
      isUsedLoanLimit = true;

      if (senderWallet.getCurrency().equals(Currency.USD)) {
        usedLoanLimitAmount = senderWallet.getBalance().negate();
      } else {
        usedLoanLimitAmount = convertCurrency(
            new ConvertAmountRequestDto(senderWallet.getCurrency().getCode(),
                Currency.USD.getCode(), senderWallet.getBalance().negate()));
      }

      senderWallet.setBalance(BigDecimal.ZERO);
    }

    CalculateCommissionQuery query = CalculateCommissionQuery.builder()
        .usedWalletOwnAmount(transferAmount.subtract(usedLoanLimitAmount))
        .loanLimitUsed(isUsedLoanLimit)
        .usedLoanLimitAmount(usedLoanLimitAmount)
        .fromWalletCurrency(senderWallet.getCurrency().getCode())
        .toWalletCurrency(receiverWallet.getCurrency().getCode())
        .transactionCurrency(transferRequestDto.getCurrency().getCode())
        .build();

    CalcCommissionResponseDto commission = queryGateway.query(query,
        CalcCommissionResponseDto.class).join();

    if (senderWallet.getCurrency().equals(Currency.USD)) {
      senderWallet.setBalance(senderWallet.getBalance().subtract(commission.getCommissionAmount()));
    } else {
      BigDecimal walletCurrencyCommission = convertCurrency(new ConvertAmountRequestDto(
          Currency.USD.getCode(),
          senderWallet.getCurrency().getCode(),
          commission.getCommissionAmount()
      ));

      senderWallet.setBalance(senderWallet.getBalance().subtract(walletCurrencyCommission));
    }

    if (senderWallet.getBalance().compareTo(BigDecimal.ZERO) < 0) {
      isUsedLoanLimit = true;

      if (senderWallet.getCurrency().equals(Currency.USD)) {
        usedLoanLimitAmount = usedLoanLimitAmount.add(senderWallet.getBalance().negate());
      } else {
        usedLoanLimitAmount = usedLoanLimitAmount.add(convertCurrency(
            new ConvertAmountRequestDto(senderWallet.getCurrency().getCode(),
                Currency.USD.getCode(), senderWallet.getBalance().negate())));
      }

      senderWallet.setBalance(BigDecimal.ZERO);
    }

    Transaction transaction = saveTransaction(senderWallet, transferRequestDto, isUsedLoanLimit,
        usedLoanLimitAmount, commission.getCommissionAmount(), TransactionStatus.PENDING);

    return transactionMapper.toTransferResponseDto(transaction);
  }

  public void updateReceiveWalletBalance(UUID walletId, BigDecimal amount, Currency operationCurrency) {
    Wallet wallet = findById(walletId);

    if (!wallet.getCurrency().equals(operationCurrency)) {
      amount = convertCurrency(new ConvertAmountRequestDto(operationCurrency.getCode(), wallet.getCurrency().getCode(), amount));
    }

    wallet.setBalance(wallet.getBalance().add(amount));

    walletRepository.save(wallet);
  }

  public void cancelTransferToAnotherWallet(UUID walletId, BigDecimal cleanAmount, Currency operationCurrency,
      BigDecimal commission) {
    Wallet wallet = findById(walletId);

    if (!wallet.getCurrency().equals(operationCurrency)) {
      cleanAmount = convertCurrency(new ConvertAmountRequestDto(operationCurrency.getCode(), wallet.getCurrency().getCode(),
          cleanAmount));
    }

    wallet.setBalance(wallet.getBalance().add(cleanAmount));

    if (!wallet.getCurrency().equals(Currency.USD)) {
      commission = convertCurrency(new ConvertAmountRequestDto(Currency.USD.getCode(), wallet.getCurrency().getCode(),
          commission));
    }

    wallet.setBalance(wallet.getBalance().add(commission));

    walletRepository.save(wallet);
  }

  private Transaction saveTransaction(Wallet wallet, WalletOperationRequestDto requestDto,
      boolean isUsedLoanLimit, BigDecimal usedLoanLimitAmount, BigDecimal commission,
      TransactionStatus status) {
    CreateTransactionCommand.CreateTransactionCommandBuilder builder = CreateTransactionCommand.builder()
        .accountId(wallet.getAccountId())
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
      builder.commission(commission);
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
