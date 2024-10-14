package com.nerdysoft.controller.loan;

import com.nerdysoft.dto.api.request.loan.LoanRequestDto;
import com.nerdysoft.entity.loan.Loan;
import com.nerdysoft.entity.loan.LoanPayment;
import com.nerdysoft.entity.security.Account;
import com.nerdysoft.service.loan.LoanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/loans")
@RequiredArgsConstructor
public class LoanController {
    private final LoanService loanService;

    @PostMapping("/apply")
    public ResponseEntity<Loan> applyForLoan(Authentication authentication, @RequestBody LoanRequestDto requestDto) {
        Account account = (Account) authentication.getPrincipal();

        return ResponseEntity.ok(loanService.applyForLoan(account.accountId(), account.email(), requestDto));
    }

    @PostMapping("/repay")
    public ResponseEntity<LoanPayment> repayForLoan(Authentication authentication) {
        Account account = (Account) authentication.getPrincipal();

        return ResponseEntity.ok(loanService.manualLoanRepay(account.accountId()));
    }
}
