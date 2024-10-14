package com.nerdysoft.service.loan.impl;

import com.nerdysoft.dto.api.request.loan.LoanRequestDto;
import com.nerdysoft.dto.api.request.loan.enums.PaymentType;
import com.nerdysoft.dto.feign.BankReserveOperationsDto;
import com.nerdysoft.dto.feign.ConvertAmountRequestDto;
import com.nerdysoft.dto.feign.TransactionRequestDto;
import com.nerdysoft.dto.feign.Wallet;
import com.nerdysoft.dto.feign.enums.Currency;
import com.nerdysoft.dto.feign.enums.ReserveType;
import com.nerdysoft.entity.loan.Loan;
import com.nerdysoft.entity.loan.LoanPayment;
import com.nerdysoft.entity.loan.enums.ApprovalStatus;
import com.nerdysoft.entity.loan.enums.RepaymentStatus;
import com.nerdysoft.feign.CurrencyExchangeFeignClient;
import com.nerdysoft.feign.WalletFeignClient;
import com.nerdysoft.repo.LoanPaymentRepository;
import com.nerdysoft.repo.LoanRepository;
import com.nerdysoft.service.analyzer.WalletBalanceAnalyzer;
import com.nerdysoft.service.feign.BankReserveFeignClient;
import com.nerdysoft.service.loan.LoanService;
import com.nerdysoft.service.loan.strategy.LoanStrategy;
import com.nerdysoft.service.loan.strategy.handlers.LoanDetails;
import com.nerdysoft.service.loan.strategy.handlers.LoanHandler;
import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LoanServiceImpl implements LoanService {
    private final CurrencyExchangeFeignClient currencyExchangeFeignClient;
    private final BankReserveFeignClient bankReserveFeignClient;
    private final WalletBalanceAnalyzer walletBalanceAnalyzer;
    private final WalletFeignClient walletFeignClient;
    private final LoanStrategy loanStrategy;
    private final LoanRepository loanRepository;
    private final LoanPaymentRepository loanPaymentRepository;

    @Transactional
    @Override
    public Loan applyForLoan(UUID accountId, String email, LoanRequestDto requestDto) {
        hasExistingLoan(accountId);

        Wallet wallet = walletFeignClient.getWalletByAccountIdAndCurrency(accountId, requestDto.currency()).getBody();

        BigDecimal maxBalanceForLastMonth = walletBalanceAnalyzer.getMaxBalanceForLastMonth(wallet.walletId());
        BigDecimal turnoverForLastMonth = walletBalanceAnalyzer.getTurnoverForLastMonth(wallet.walletId(), wallet.currency());

        LoanHandler loanHandler = loanStrategy.get(
                convertToUsd(wallet.currency(), maxBalanceForLastMonth),
                convertToUsd(wallet.currency(), turnoverForLastMonth)
        );

        BigDecimal usdLoanAmount = convertToUsd(requestDto);
        LoanDetails loanDetails = loanHandler.getLoan(usdLoanAmount);

        BigDecimal walletCurrencyRepaymentAmount = convert(Currency.USD, wallet.currency(), loanDetails.getRepaymentAmount());

        Loan loan = Loan.builder()
                .accountId(accountId)
                .accountEmail(email)
                .walletId(wallet.walletId())
                .walletCurrency(wallet.currency())
                .approvalStatus(loanDetails.getApprovalStatus())
                .paymentType(requestDto.paymentType())
                .walletCurrencyLoanAmount(requestDto.requestedAmount())
                .usdLoanAmount(usdLoanAmount)
                .interestRate(loanDetails.getInterestRate().multiply(BigDecimal.valueOf(100)))
                .usdRepaymentAmount(loanDetails.getRepaymentAmount())
                .walletCurrencyRepaymentAmount(walletCurrencyRepaymentAmount)
                .usdRemainingRepaymentAmount(loanDetails.getRepaymentAmount())
                .repaymentTermInMonths(loanDetails.getRepaymentTermInMonths())
                .usdMonthlyRepaymentAmount((loanDetails.getApprovalStatus().equals(ApprovalStatus.REJECTED))
                        ? BigDecimal.ZERO
                        : loanDetails.getRepaymentAmount().divide(loanDetails.getRepaymentTermInMonths(), 2, RoundingMode.HALF_UP))
                .totalPaymentsMade(BigDecimal.ZERO)
                .repaymentStatus(RepaymentStatus.PENDING)
                .timestamp(LocalDateTime.now())
                .nextPayment((loanDetails.getApprovalStatus().equals(ApprovalStatus.REJECTED)) ? null : LocalDate.now().plusMonths(1))
                .dueDate((loanDetails.getApprovalStatus().equals(ApprovalStatus.REJECTED)) ? null : LocalDateTime.now().plusMonths(loanDetails.getRepaymentTermInMonths().intValue()))
                .build();

        if (loan.getApprovalStatus().equals(ApprovalStatus.APPROVED)) {
            bankReserveFeignClient.withdraw(new BankReserveOperationsDto(ReserveType.LOAN, loan.getUsdLoanAmount()));
            walletFeignClient.deposit(wallet.walletId(), new TransactionRequestDto(loan.getWalletCurrencyLoanAmount(), wallet.currency()));
        }

        return loanRepository.save(loan);
    }

    @Transactional
    @Override
    public LoanPayment manualLoanRepay(UUID accountId) {
        isCurrentMonthPaymentCommitted(accountId);

        Loan loan = loanRepository.findByAccountIdAndRepaymentStatus(accountId, RepaymentStatus.PENDING)
                .orElseThrow(() -> new EntityNotFoundException("Can't find loan with account ID: " + accountId));

        return commitPayment(loan);
    }

    @Transactional
    @Override
    @Scheduled(cron = "0 0 8 * * ?")
    public void autoLoanRepay() {
        List<Loan> loans = loanRepository.findAllByApprovalStatusAndPaymentTypeAndRepaymentStatusAndNextPayment(
                ApprovalStatus.APPROVED,
                PaymentType.AUTO,
                RepaymentStatus.PENDING,
                LocalDate.now()
        );

        loans.forEach(this::commitPayment);
        loanRepository.saveAll(loans);
    }

    private void hasExistingLoan(UUID accountId) {
        if (loanRepository.existsByAccountIdAndApprovalStatusAndRepaymentStatus(accountId, ApprovalStatus.APPROVED, RepaymentStatus.PENDING)) {
            throw new IllegalStateException("Account with ID: " + accountId + " already has an active loan");
        }
    }

    private LoanPayment commitPayment(Loan loan) {
        loan.setUsdRemainingRepaymentAmount(loan.getUsdRemainingRepaymentAmount().subtract(loan.getUsdMonthlyRepaymentAmount()));

        walletFeignClient.withdraw(loan.getWalletId(), new TransactionRequestDto(loan.getWalletCurrencyRepaymentAmount(), loan.getWalletCurrency()));

        loan.setTotalPaymentsMade(loan.getTotalPaymentsMade().add(BigDecimal.ONE));
        loan.setNextPayment(loan.getNextPayment().plusMonths(1));

        if (loan.getTotalPaymentsMade().compareTo(loan.getRepaymentTermInMonths()) == 0) {
            loan.setRepaymentStatus(RepaymentStatus.COMPLETED);
        }

        return loanPaymentRepository.save(new LoanPayment(loan));
    }

    private void isCurrentMonthPaymentCommitted(UUID accountId) {
        if (loanPaymentRepository.existsByAccountIdAndTimestampBetween(
                accountId,
                YearMonth.now().atDay(1).atStartOfDay(),
                YearMonth.now().atEndOfMonth().atTime(23, 59, 59))) {
            throw new IllegalStateException("Current month loan repayment for account with ID: " + accountId + " is already committed");
        }
    }

    private BigDecimal convertToUsd(LoanRequestDto loanRequestDto) {
        return (loanRequestDto.currency().equals(Currency.USD)) ? loanRequestDto.requestedAmount() : currencyExchangeFeignClient
                .convert(new ConvertAmountRequestDto(loanRequestDto.currency().getCode(), Currency.USD.getCode(), loanRequestDto.requestedAmount()))
                .getBody()
                .convertedAmount();
    }

    private BigDecimal convertToUsd(Currency fromCurrency, BigDecimal amount) {
        return (fromCurrency.equals(Currency.USD)) ? amount : currencyExchangeFeignClient
                .convert(new ConvertAmountRequestDto(fromCurrency.getCode(), Currency.USD.getCode(), amount))
                .getBody()
                .convertedAmount();
    }

    private BigDecimal convert(Currency fromCurrency, Currency toCurrency, BigDecimal amount) {
        return (toCurrency.equals(Currency.USD)) ? amount : currencyExchangeFeignClient
                .convert(new ConvertAmountRequestDto(fromCurrency.getCode(), toCurrency.getCode(), amount))
                .getBody()
                .convertedAmount();
    }
}
