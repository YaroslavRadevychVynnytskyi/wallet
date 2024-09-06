package com.application.controller;

import com.application.dto.AccountResponseDto;
import com.application.dto.CreateAccountRequestDto;
import com.application.dto.UpdateAccountRequestDto;
import com.application.dto.UpdatedAccountResponseDto;
import com.application.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Account management", description = "Endpoints for managing accounts")
@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class AccountController {
    private final AccountService accountService;

    @Operation(summary = "Create new account")
    @PostMapping
    public ResponseEntity<AccountResponseDto> create(@RequestBody CreateAccountRequestDto requestDto) {
        return ResponseEntity.ok(accountService.create(requestDto));
    }

    @Operation(summary = "Get account data by ID")
    @GetMapping("/{accountId}")
    public ResponseEntity<AccountResponseDto> getById(@PathVariable UUID accountId) {
        return ResponseEntity.ok(accountService.getById(accountId));
    }

    @Operation(summary = "Update account data by ID")
    @PutMapping("/{accountId}")
    public ResponseEntity<UpdatedAccountResponseDto> update(@PathVariable UUID accountId, @RequestBody UpdateAccountRequestDto requestDto) {
        return ResponseEntity.ok(accountService.update(accountId, requestDto));
    }

    @Operation(summary = "Delete account by ID")
    @DeleteMapping("/{accountId}")
    public ResponseEntity<Void> delete(@PathVariable UUID accountId) {
        accountService.deleteById(accountId);
        return ResponseEntity.noContent().build();
    }
}
