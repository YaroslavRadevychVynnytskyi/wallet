package com.nerdysoft.controller.loanlimit;

import com.nerdysoft.entity.loanlimit.LoanLimit;
import com.nerdysoft.entity.security.Account;
import com.nerdysoft.model.enums.Currency;
import com.nerdysoft.service.loanlimit.LoanLimitService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/loan-limits")
@RequiredArgsConstructor
public class LoanLimitController {
    private final LoanLimitService loanLimitService;

    @PostMapping("/take")
    public ResponseEntity<LoanLimit> takeLoanLimit(Authentication authentication, @RequestParam Currency currency) {
        Account account = (Account) authentication.getPrincipal();

        return ResponseEntity.ok(loanLimitService.getLoanLimit(account.accountId(), account.email(), currency));
    }

    @PostMapping("/repay")
    public ResponseEntity<LoanLimit> repayLoanLimit(Authentication authentication) {
        Account account = (Account) authentication.getPrincipal();

        return ResponseEntity.ok(loanLimitService.repayLoanLimit(account.accountId()));
    }

    @GetMapping("/{walletId}")
    public ResponseEntity<LoanLimit> getLoanLimitByWalletId(@PathVariable UUID walletId) {
        return ResponseEntity.ok(loanLimitService.getLoanLimitByWalletId(walletId));
    }

    @PutMapping("/{walletId}")
    public ResponseEntity<LoanLimit> updateByWalletId(@PathVariable UUID walletId, @RequestBody LoanLimit loanLimit) {
        return ResponseEntity.ok(loanLimitService.updateByWalletId(walletId, loanLimit));
    }
}
