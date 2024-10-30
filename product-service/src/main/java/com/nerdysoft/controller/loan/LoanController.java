package com.nerdysoft.controller.loan;

import com.nerdysoft.axon.command.loan.ApplyLoanCommand;
import com.nerdysoft.axon.command.loan.RepayLoanCommand;
import com.nerdysoft.axon.query.FindLoanByIdQuery;
import com.nerdysoft.dto.api.request.loan.LoanRequestDto;
import com.nerdysoft.entity.loan.Loan;
import com.nerdysoft.entity.security.Account;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.beans.BeanUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/loans")
@RequiredArgsConstructor
public class LoanController {
    private final CommandGateway commandGateway;
    private final QueryGateway queryGateway;

    @PostMapping("/apply")
    public ResponseEntity<Loan> applyForLoan(Authentication authentication, @RequestBody LoanRequestDto requestDto) {
        Account account = (Account) authentication.getPrincipal();

        ApplyLoanCommand applyLoanCommand = new ApplyLoanCommand();
        BeanUtils.copyProperties(requestDto, applyLoanCommand);
        applyLoanCommand.setAccountId(account.accountId());
        applyLoanCommand.setAccountEmail(account.getUsername());

        UUID loanId = commandGateway.sendAndWait(applyLoanCommand);
        Loan loan = queryGateway.query(new FindLoanByIdQuery(loanId), Loan.class).join();

        return ResponseEntity.ok(loan);
    }

    @PostMapping("/repay/{loanId}")
    public ResponseEntity<UUID> repayForLoan(Authentication authentication, @PathVariable UUID loanId) {
        Account account = (Account) authentication.getPrincipal();
        RepayLoanCommand repayLoanCommand = new RepayLoanCommand(loanId, account.accountId());

        return ResponseEntity.ok(commandGateway.sendAndWait(repayLoanCommand));
    }
}
