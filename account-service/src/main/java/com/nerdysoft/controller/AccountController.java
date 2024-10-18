package com.nerdysoft.controller;

import com.nerdysoft.annotation.BasicInfoController;
import com.nerdysoft.dto.api.request.CreateAccountRequestDto;
import com.nerdysoft.dto.api.request.CreateTransactionRequestDto;
import com.nerdysoft.dto.api.request.GenericTransactionRequestDto;
import com.nerdysoft.dto.api.request.UpdateAccountRequestDto;
import com.nerdysoft.dto.api.response.AccountResponseDto;
import com.nerdysoft.dto.api.response.GenericTransactionResponseDto;
import com.nerdysoft.dto.api.response.TransactionResponseDto;
import com.nerdysoft.dto.api.response.UpdatedAccountResponseDto;
import com.nerdysoft.dto.api.response.UserDetailsDto;
import com.nerdysoft.dto.feign.Currency;
import com.nerdysoft.entity.Account;
import com.nerdysoft.mapper.AccountMapper;
import com.nerdysoft.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetailsService;
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
@BasicInfoController(basicField = "fullName", databaseType = BasicInfoController.DatabaseType.POSTGRES)
public class AccountController {
    private final AccountService accountService;

    private final AccountMapper accountMapper;

    private final UserDetailsService userDetailsService;

    @Operation(summary = "Create new account")
    @PostMapping
    public ResponseEntity<AccountResponseDto> create(@RequestBody CreateAccountRequestDto requestDto) {
        return ResponseEntity.ok(accountMapper.toDto(accountService.create(requestDto)));
    }

    @Operation(summary = "Get account data by ID")
    @GetMapping("/{accountId}")
    public ResponseEntity<AccountResponseDto> getById(@PathVariable UUID accountId) {
        return ResponseEntity.ok(accountService.getById(accountId));
    }

    @Operation(summary = "Update account data by ID")
    @PutMapping("/{accountId}")
    public ResponseEntity<UpdatedAccountResponseDto> update(
            @PathVariable UUID accountId,
            @RequestBody UpdateAccountRequestDto requestDto) {
        return ResponseEntity.ok(accountService.update(accountId, requestDto));
    }

    @Operation(summary = "Delete account by ID")
    @DeleteMapping("/{accountId}")
    public ResponseEntity<Void> delete(@PathVariable UUID accountId) {
        accountService.deleteById(accountId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{accountId}/transactions")
    public ResponseEntity<TransactionResponseDto> createTransaction(
            @PathVariable UUID accountId,
            @RequestBody CreateTransactionRequestDto requestDto,
            @RequestParam Currency fromWalletCurrency,
            @RequestParam Currency toWalletCurrency) {
        return ResponseEntity.ok(accountService.createTransaction(accountId, requestDto, fromWalletCurrency, toWalletCurrency));
    }

    @PostMapping("/{accountId}/update-balance")
    public ResponseEntity<GenericTransactionResponseDto> updateBalance(@PathVariable UUID accountId,
                                                                       @RequestBody GenericTransactionRequestDto requestDto) {
        return ResponseEntity.ok(accountService.updateBalance(accountId, requestDto));
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<UserDetailsDto> getAccountByEmail(@PathVariable String email) {
        return ResponseEntity.ok(accountMapper.toUserDetailsDto(
            (Account) userDetailsService.loadUserByUsername(email)
        ));
    }
}
