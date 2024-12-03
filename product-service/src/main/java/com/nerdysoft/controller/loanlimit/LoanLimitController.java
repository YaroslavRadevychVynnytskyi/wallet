package com.nerdysoft.controller.loanlimit;

import com.nerdysoft.axon.command.loanlimit.RepayLoanLimitCommand;
import com.nerdysoft.axon.command.loanlimit.TakeLoanLimitCommand;
import com.nerdysoft.axon.query.FindLoanLimitByIdQuery;
import com.nerdysoft.axon.query.loanlimit.FindLoanLimitByAccountIdQuery;
import com.nerdysoft.entity.loanlimit.LoanLimit;
import com.nerdysoft.entity.security.Account;
import com.nerdysoft.model.enums.RoleName;
import com.nerdysoft.model.exception.UniqueException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/loan-limits")
@RequiredArgsConstructor
public class LoanLimitController {
    private final CommandGateway commandGateway;
    private final QueryGateway queryGateway;

    @PostMapping("/take")
    public ResponseEntity<LoanLimit> takeLoanLimit(Authentication authentication) {
        Account account = (Account) authentication.getPrincipal();

        TakeLoanLimitCommand takeLoanLimitCommand = new TakeLoanLimitCommand(account.accountId(), account.email());

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

    @GetMapping("/{accountId}")
    public ResponseEntity<LoanLimit> findLoanLimitByAccountId(Authentication authentication, @PathVariable UUID accountId) {
        Account account = (Account) authentication.getPrincipal();

        if (account.getAuthorities().stream().anyMatch(a -> a.name().equals(RoleName.ADMIN.getName()))
        || account.accountId().equals(accountId)) {
            LoanLimit loanLimit = queryGateway.query(new FindLoanLimitByAccountIdQuery(account.accountId()), LoanLimit.class).join();

            return ResponseEntity.ok(loanLimit);
        } else {
            throw new UniqueException("You don't have a permission for this operation", HttpStatus.FORBIDDEN);
        }
    }
}
