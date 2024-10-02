package com.nerdysoft.controller.loanlimit;

import com.nerdysoft.entity.loanlimit.LoanLimit;
import com.nerdysoft.service.analyzer.AccountBalanceAnalyzer;
import com.nerdysoft.service.loanlimit.LoanLimitService;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/loan-limits")
@RequiredArgsConstructor
public class LoanLimitController {
    private final LoanLimitService loanLimitService;

    private final AccountBalanceAnalyzer accountBalanceAnalyzer;

    @GetMapping("/{accountId}")
    public ResponseEntity<LoanLimit> getLoanLimit(@PathVariable UUID accountId) {
        return ResponseEntity.ok(loanLimitService.getLoanLimit(accountId));
    }

    @GetMapping("/turnover/{accountId}")
    public boolean getTurnover(@PathVariable UUID accountId) {
        return accountBalanceAnalyzer.hasTurnoverAboveForLastMonth(accountId, BigDecimal.valueOf(2000));
    }

    @GetMapping("/balance-over/{accountId}/{threshold}")
    public boolean hasBalanceOverForLastMonth(@PathVariable UUID accountId, @PathVariable BigDecimal threshold) {
        return accountBalanceAnalyzer.hasBalanceAboveForLastMonth(accountId, threshold);
    }
}
