package com.nerdysoft.service.deposit.impl;

import com.nerdysoft.dto.feign.BankReserveTypeDto;
import com.nerdysoft.dto.feign.ConvertAmountRequestDto;
import com.nerdysoft.dto.feign.TransactionRequestDto;
import com.nerdysoft.dto.feign.UpdateBalanceDto;
import com.nerdysoft.dto.feign.Wallet;
import com.nerdysoft.entity.deposit.Deposit;
import com.nerdysoft.feign.BankReserveFeignClient;
import com.nerdysoft.feign.CurrencyExchangeFeignClient;
import com.nerdysoft.feign.WalletFeignClient;
import com.nerdysoft.model.enums.Currency;
import com.nerdysoft.model.enums.DepositStatus;
import com.nerdysoft.model.enums.OperationType;
import com.nerdysoft.model.enums.ReserveType;
import com.nerdysoft.repo.deposit.DepositRepository;
import com.nerdysoft.service.deposit.DepositService;
import com.nerdysoft.service.email.NotificationService;
import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DepositServiceImpl implements DepositService {
    private final DepositRepository depositRepository;
    private final WalletFeignClient walletFeignClient;
    private final NotificationService notificationService;
    private final BankReserveFeignClient bankReserveFeignClient;
    private final CurrencyExchangeFeignClient currencyExchangeFeignClient;

    @Value("${mail.subject.deposit}")
    private String emailSubject;

    @Value("${mail.body.deposit}")
    private String emailBody;

    @Transactional
    @Override
    public Deposit applyDeposit(UUID accountId, String accountEmail, BigDecimal amount, Currency walletCurrency) {
        checkForExistingDeposits(accountId);

        Wallet wallet = walletFeignClient.getWalletByAccountIdAndCurrency(accountId, walletCurrency).getBody();
        checkIfEnoughMoney(wallet, amount);

        Deposit deposit = Deposit.builder()
                .accountId(accountId)
                .accountEmail(accountEmail)
                .walletId(wallet.walletId())
                .amount(convert(walletCurrency, wallet.currency(), amount))
                .currency(wallet.currency())
                .depositDate(LocalDate.now())
                .maturityDate(LocalDate.now().plusMonths(4))
                .yearInterestRate(BigDecimal.valueOf(0.05))
                .fourMonthsInterestRate(BigDecimal.valueOf(0.05).divide(BigDecimal.valueOf(3), 2, RoundingMode.HALF_DOWN))
                .depositStatus(DepositStatus.FROZEN)
                .build();

        return depositRepository.save(deposit);
    }

    @Transactional
    @Override
    public Deposit withdrawDeposit(UUID accountId) {
        Deposit deposit = depositRepository.findByAccountIdAndDepositStatus(accountId, DepositStatus.AVAILABLE_FOR_WITHDRAWAL)
                .orElseThrow(() -> new EntityNotFoundException("There are no available for withdraw deposits for account with ID: " + accountId));

        //TODO: carry this logic out to the WithdrawDepositSaga
        /*
        BigDecimal amount = deposit.getAmount();
        walletFeignClient.deposit(deposit.getWalletId(), new TransactionRequestDto(amount, deposit.getCurrency()));

        UUID bankReserveId = bankReserveFeignClient.getReserveIdByType(new BankReserveTypeDto(ReserveType.DEPOSIT)).getBody();
        bankReserveFeignClient.updateBalance(new UpdateBalanceDto(bankReserveId, ReserveType.DEPOSIT, convertToUsd(deposit.getCurrency(), amount), OperationType.WITHDRAW));

         */

        deposit.setAmount(BigDecimal.ZERO);
        deposit.setMaturityDate(null);
        deposit.setNotificationDate(null);
        deposit.setDepositStatus(DepositStatus.INACTIVE);

        return depositRepository.save(deposit);
    }

    @Override
    public void deleteById(UUID depositId) {
        depositRepository.deleteById(depositId);
    }

    @Override
    public void cancelWithdrawDeposit(UUID depositId,
                                      BigDecimal amount,
                                      LocalDate maturityDate,
                                      LocalDate notificationDate,
                                      DepositStatus depositStatus) {
        Deposit deposit = depositRepository.findById(depositId).orElseThrow(() ->
                new EntityNotFoundException("Can't find deposit with ID: " + depositId));

        deposit.setAmount(amount);
        deposit.setMaturityDate(maturityDate);
        deposit.setNotificationDate(notificationDate);
        deposit.setDepositStatus(depositStatus);

        depositRepository.save(deposit);
    }

    @Transactional
    @Scheduled(cron = "0 0 8 * * ?")
    public void processMaturedDeposits() {
        List<Deposit> matureDeposits = depositRepository.findByMaturityDateEquals(LocalDate.now());

        matureDeposits.forEach(d -> {
            updateDepositAmount(d);
            d.setDepositStatus(DepositStatus.AVAILABLE_FOR_WITHDRAWAL);
            d.setMaturityDate(null);
            notificationService.sendEmail(d.getAccountEmail(), emailSubject, emailBody);
            d.setNotificationDate(LocalDate.now());
        });

        depositRepository.saveAll(matureDeposits);
    }

    @Transactional
    @Scheduled(cron = "0 0 8 * * ?")
    public void processDepositsToFreeze() {
        List<Deposit> depositsToFreeze = depositRepository.findDepositsToFreeze(
                LocalDate.now().minusDays(10),
                DepositStatus.AVAILABLE_FOR_WITHDRAWAL
        );

        depositsToFreeze.forEach(d -> {
            d.setDepositStatus(DepositStatus.FROZEN);
            d.setMaturityDate(LocalDate.now().plusMonths(4));
        });

        depositRepository.saveAll(depositsToFreeze);
    }

    private void checkForExistingDeposits(UUID accountId) {
        if (depositRepository.existsByAccountIdAndDepositStatusNot(accountId, DepositStatus.INACTIVE)) {
            throw new IllegalStateException("Account with ID: " + accountId + " already has an active deposit");
        }
    }

    private void checkIfEnoughMoney(Wallet wallet, BigDecimal amount) {
        if (wallet.balance().compareTo(amount) < 0) {
            throw new IllegalStateException("Insufficient balance for wallet with ID: " + wallet.walletId());
        }
    }

    private void updateDepositAmount(Deposit deposit) {
        BigDecimal oldAmount = deposit.getAmount();

        BigDecimal earnedInterest = oldAmount.multiply(deposit.getFourMonthsInterestRate());
        BigDecimal newAmount = oldAmount.add(earnedInterest);

        UUID bankReserveId = bankReserveFeignClient.getReserveIdByType(new BankReserveTypeDto(ReserveType.DEPOSIT)).getBody();
        bankReserveFeignClient.updateBalance(new UpdateBalanceDto(bankReserveId, ReserveType.DEPOSIT, convertToUsd(deposit.getCurrency(), earnedInterest), OperationType.WITHDRAW));

        deposit.setAmount(newAmount);
    }

    private BigDecimal convertToUsd(Currency fromCurrency, BigDecimal amount) {
        return (fromCurrency.equals(Currency.USD)) ? amount : currencyExchangeFeignClient
                .convert(new ConvertAmountRequestDto(fromCurrency.getCode(), Currency.USD.getCode(), amount))
                .getBody()
                .convertedAmount();
    }

    private BigDecimal convert(Currency fromCurrency, Currency toCurrency, BigDecimal amount) {
        return (toCurrency.equals(fromCurrency)) ? amount : currencyExchangeFeignClient
                .convert(new ConvertAmountRequestDto(fromCurrency.getCode(), toCurrency.getCode(), amount))
                .getBody()
                .convertedAmount();
    }
}
