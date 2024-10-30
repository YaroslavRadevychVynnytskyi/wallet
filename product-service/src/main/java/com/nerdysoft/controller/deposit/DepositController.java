package com.nerdysoft.controller.deposit;

import com.nerdysoft.axon.command.deposit.ApplyDepositCommand;
import com.nerdysoft.axon.command.deposit.WithdrawDepositCommand;
import com.nerdysoft.axon.query.FindDepositByIdQuery;
import com.nerdysoft.dto.api.request.deposit.DepositRequestDto;
import com.nerdysoft.entity.deposit.Deposit;
import com.nerdysoft.entity.security.Account;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/deposits")
@RequiredArgsConstructor
public class DepositController {
    private final CommandGateway commandGateway;
    private final QueryGateway queryGateway;

    @PostMapping("/apply")
    public ResponseEntity<Deposit> applyDeposit(Authentication authentication, @RequestBody DepositRequestDto requestDto) {
        Account account = (Account) authentication.getPrincipal();

        ApplyDepositCommand applyDepositCommand = ApplyDepositCommand.builder()
                .accountId(account.accountId())
                .accountEmail(account.email())
                .amount(requestDto.getAmount())
                .walletCurrency(requestDto.getWalletCurrency())
                .build();

        UUID depositId = commandGateway.sendAndWait(applyDepositCommand);
        Deposit deposit = queryGateway.query(new FindDepositByIdQuery(depositId), Deposit.class).join();

        return ResponseEntity.ok(deposit);
    }

    @PostMapping("/withdraw/{depositId}")
    public ResponseEntity<Deposit> withdrawDeposit(Authentication authentication, @PathVariable UUID depositId) {
        Account account = (Account) authentication.getPrincipal();

        WithdrawDepositCommand withdrawDepositCommand = new WithdrawDepositCommand(depositId, account.accountId());
        commandGateway.sendAndWait(withdrawDepositCommand);
        Deposit deposit = queryGateway.query(new FindDepositByIdQuery(depositId), Deposit.class).join();

        return ResponseEntity.ok(deposit);
    }
}
