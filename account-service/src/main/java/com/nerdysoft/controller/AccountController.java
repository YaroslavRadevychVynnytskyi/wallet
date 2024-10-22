package com.nerdysoft.controller;

import com.nerdysoft.axon.command.account.CreateAccountCommand;
import com.nerdysoft.axon.command.account.DeleteAccountCommand;
import com.nerdysoft.axon.command.account.UpdateAccountCommand;
import com.nerdysoft.axon.query.account.FindAccountByIdQuery;
import com.nerdysoft.axon.query.account.FindUserDetailsQuery;
import com.nerdysoft.dto.api.request.CreateAccountRequestDto;
import com.nerdysoft.dto.api.request.CreateTransactionRequestDto;
import com.nerdysoft.dto.api.request.GenericTransactionRequestDto;
import com.nerdysoft.dto.api.request.UpdateAccountRequestDto;
import com.nerdysoft.dto.api.response.AccountResponseDto;
import com.nerdysoft.dto.api.response.GenericTransactionResponseDto;
import com.nerdysoft.dto.api.response.TransactionResponseDto;
import com.nerdysoft.dto.api.response.UpdatedAccountResponseDto;
import com.nerdysoft.mapper.AccountMapper;
import com.nerdysoft.model.Account;
import com.nerdysoft.model.enums.Currency;
import com.nerdysoft.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Account management", description = "Endpoints for managing accounts")
@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class AccountController {
    private final CommandGateway commandGateway;

    private final QueryGateway queryGateway;

    private final AccountMapper accountMapper;

    private final TransactionService transactionService;

    @Operation(summary = "Create new account")
    @PostMapping
    public ResponseEntity<AccountResponseDto> create(@RequestBody CreateAccountRequestDto dto) {
        CreateAccountCommand command = new CreateAccountCommand(dto.email(), dto.fullName(), dto.password());
        UUID accountId = commandGateway.sendAndWait(command);
        Account account = queryGateway.query(new FindAccountByIdQuery(accountId), Account.class).join();
        return ResponseEntity.ok(accountMapper.toDto(account));
    }

    @Operation(summary = "Get account data by ID")
    @GetMapping("/{accountId}")
    public ResponseEntity<AccountResponseDto> findById(@PathVariable UUID accountId) {
        return ResponseEntity.ok(accountMapper.toDto(queryGateway.query(new FindAccountByIdQuery(accountId), Account.class).join()));
    }

    @Operation(summary = "Get account data by email")
    @GetMapping("/email/{email}")
    public ResponseEntity<Account> findByEmail(@PathVariable String email) {
        return ResponseEntity.ok(queryGateway.query(new FindUserDetailsQuery(email), Account.class).join());
    }

//    @Operation(summary = "Get account data by email")
//    @GetMapping("/email/{email}")
//    public ResponseEntity<UserDetailsDto> getAccountByEmail(@PathVariable String email) {
//        return ResponseEntity.ok(accountMapper.toUserDetailsDto(
//            (Account) userDetailsService.loadUserByUsername(email)
//        ));
//    }
//
    @Operation(summary = "Update account data by ID")
    @PutMapping("/{accountId}")
    public ResponseEntity<UpdatedAccountResponseDto> update(
            @PathVariable UUID accountId,
            @RequestBody UpdateAccountRequestDto dto) {
        Account account = commandGateway.sendAndWait(new UpdateAccountCommand(accountId, dto.fullName(),
            dto.email()));
        return ResponseEntity.ok(accountMapper.toUpdateResponseDto(account));
    }

    @Operation(summary = "Delete account by ID")
    @DeleteMapping("/{accountId}")
    public ResponseEntity<Void> delete(@PathVariable UUID accountId) {
        commandGateway.sendAndWait(new DeleteAccountCommand(accountId));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{accountId}/transactions")
    public ResponseEntity<TransactionResponseDto> createTransaction(
            @PathVariable UUID accountId,
            @RequestBody CreateTransactionRequestDto requestDto,
            @RequestParam Currency fromWalletCurrency,
            @RequestParam Currency toWalletCurrency) {
        return ResponseEntity.ok(transactionService.createTransaction(accountId, requestDto, fromWalletCurrency, toWalletCurrency));
    }

    @PostMapping("/{accountId}/update-balance")
    public ResponseEntity<GenericTransactionResponseDto> updateBalance(@PathVariable UUID accountId,
                                                                       @RequestBody GenericTransactionRequestDto requestDto) {
        return ResponseEntity.ok(transactionService.updateBalance(accountId, requestDto));
    }
}
