package com.nerdysoft.service.loanlimit.impl;

import com.nerdysoft.entity.loanlimit.LoanLimit;
import com.nerdysoft.model.exception.UniqueException;
import com.nerdysoft.repo.loanlimit.LoanLimitRepository;
import com.nerdysoft.service.analyzer.WalletBalanceAnalyzer;
import com.nerdysoft.service.loanlimit.LoanLimitService;
import com.nerdysoft.service.loanlimit.strategy.LoanLimitStrategy;
import com.nerdysoft.service.loanlimit.strategy.handlers.LoanLimitHandler;
import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LoanLimitServiceImpl implements LoanLimitService {
    private final LoanLimitRepository loanLimitRepository;
    private final WalletBalanceAnalyzer walletBalanceAnalyzer;
    private final LoanLimitStrategy loanLimitStrategy;

    @Override
    public LoanLimit findById(UUID id) {
        return loanLimitRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Loan limit not found"));
    }

    @Override
    public LoanLimit getLoanLimit(UUID accountId, String email) {
        if (!loanLimitRepository.checkIfExistsNotRepaidLoanLimitByAccountId(accountId)) {
            BigDecimal maxBalanceForLastMonth = walletBalanceAnalyzer.getMaxBalanceForLastMonthByAccount(accountId);
            BigDecimal turnoverForLastMonth = walletBalanceAnalyzer.getTurnoverForLastMonthByAccount(accountId);

            LoanLimitHandler loanLimitHandler = loanLimitStrategy.get(
                maxBalanceForLastMonth,
                turnoverForLastMonth
            );

            LoanLimit loanLimit = LoanLimit.builder()
                .accountId(accountId)
                .email(email)
                .availableAmount(loanLimitHandler.getLoanLimit())
                .initialAmount(loanLimitHandler.getLoanLimit())
                .repaid(false)
                .build();

            return loanLimitRepository.save(loanLimit);
        } else {
            throw new UniqueException("Account has already a loan limit", HttpStatus.NOT_ACCEPTABLE);
        }
    }

    @Override
    public LoanLimit getLoanLimitByAccountId(UUID accountId) {
        return loanLimitRepository.findNotRepaidLoanLimitByAccountId(accountId)
                .orElseThrow(() -> new EntityNotFoundException("Can't find loan limit"));
    }

    @Override
    public LoanLimit subtractAvailableLoanLimitAmount(UUID loanLimitId, BigDecimal usedLoanLimitAmount) {
        LoanLimit loanLimit = findById(loanLimitId);

        loanLimit.setAvailableAmount(loanLimit.getAvailableAmount().subtract(usedLoanLimitAmount));

        return loanLimitRepository.save(loanLimit);
    }

    @Transactional
    @Override
    public LoanLimit repayLoanLimit(UUID loanLimitId) {
        LoanLimit loanLimit = findById(loanLimitId);
//
//        walletFeignClient.withdraw(loanLimit.getWalletId(), new TransactionRequestDto(loanLimit.getInitialAmount(), loanLimit.getCurrency()));
//
//        UUID bankReserveId = bankReserveFeignClient.getReserveIdByType(new BankReserveTypeDto(ReserveType.LOAN_LIMIT)).getBody();
//        bankReserveFeignClient.updateBalance(new UpdateBalanceDto(bankReserveId, ReserveType.LOAN_LIMIT, loanLimit.getInitialAmount(), OperationType.DEPOSIT));

        loanLimit.setAvailableAmount(BigDecimal.ZERO);
//        loanLimit.setRepaid(true);

        return loanLimitRepository.save(loanLimit);
    }

    @Override
    public LoanLimit cancelUpdateLoanLimit(UUID loanLimitId, BigDecimal usedAvailableAmount) {
        LoanLimit loanLimit = findById(loanLimitId);

        loanLimit.setAvailableAmount(loanLimit.getAvailableAmount().add(usedAvailableAmount));

        return loanLimitRepository.save(loanLimit);
    }
}
