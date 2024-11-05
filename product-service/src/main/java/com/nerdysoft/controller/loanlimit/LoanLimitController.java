package com.nerdysoft.controller.loanlimit;

import com.nerdysoft.axon.command.loanlimit.RepayLoanLimitCommand;
import com.nerdysoft.axon.command.loanlimit.TakeLoanLimitCommand;
import com.nerdysoft.axon.query.FindLoanLimitByIdQuery;
import com.nerdysoft.axon.query.FindLoanLimitByWalletIdQuery;
import com.nerdysoft.entity.loanlimit.LoanLimit;
import com.nerdysoft.entity.security.Account;
import com.nerdysoft.model.enums.Currency;
import com.nerdysoft.service.loanlimit.LoanLimitService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.queryhandling.QueryGateway;
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
    private final CommandGateway commandGateway;
    private final QueryGateway queryGateway;
    private final LoanLimitService loanLimitService;

    @PostMapping("/take")
    public ResponseEntity<LoanLimit> takeLoanLimit(Authentication authentication, @RequestParam Currency currency) {
        Account account = (Account) authentication.getPrincipal();

        TakeLoanLimitCommand takeLoanLimitCommand = TakeLoanLimitCommand.builder()
                .accountId(account.accountId())
                .accountEmail(account.getUsername())
                .currency(currency)
                .build();

        UUID loanLimitId = commandGateway.sendAndWait(takeLoanLimitCommand);
        LoanLimit loanLimit = queryGateway.query(new FindLoanLimitByIdQuery(loanLimitId), LoanLimit.class).join();

        return ResponseEntity.ok(loanLimit);
    }

    @PostMapping("/repay/{loanLimitId}")
    public ResponseEntity<UUID> repayLoanLimit(Authentication authentication, @PathVariable UUID loanLimitId) {
        Account account = (Account) authentication.getPrincipal();
        RepayLoanLimitCommand repayLoanLimitCommand = new RepayLoanLimitCommand(loanLimitId, account.accountId());

        return ResponseEntity.ok(commandGateway.sendAndWait(repayLoanLimitCommand));
    }

    @GetMapping("/{walletId}")
    public ResponseEntity<LoanLimit> getLoanLimitByWalletId(@PathVariable UUID walletId) {
        LoanLimit loanLimit = queryGateway.query(new FindLoanLimitByWalletIdQuery(walletId), LoanLimit.class).join();

        return ResponseEntity.ok(loanLimit);
    }

    @PutMapping("/{walletId}")
    public ResponseEntity<LoanLimit> updateByWalletId(@PathVariable UUID walletId, @RequestBody LoanLimit loanLimit) {
        return ResponseEntity.ok(loanLimitService.updateByWalletId(walletId, loanLimit));
    }
}
