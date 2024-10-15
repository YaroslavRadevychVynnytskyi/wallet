package com.nerdysoft.controller.deposit;

import com.nerdysoft.dto.api.request.deposit.DepositRequestDto;
import com.nerdysoft.entity.deposit.Deposit;
import com.nerdysoft.entity.security.Account;
import com.nerdysoft.service.deposit.DepositService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/deposits")
@RequiredArgsConstructor
public class DepositController {
    private final DepositService depositService;

    @PostMapping("/apply")
    public ResponseEntity<Deposit> applyDeposit(Authentication authentication, @RequestBody DepositRequestDto requestDto) {
        Account account = (Account) authentication.getPrincipal();

        return ResponseEntity.ok(depositService.applyDeposit(account.accountId(), account.getUsername(), requestDto));
    }

    @PostMapping("/withdraw")
    public ResponseEntity<Deposit> withdrawDeposit(Authentication authentication) {
        Account account = (Account) authentication.getPrincipal();

        return ResponseEntity.ok(depositService.withdrawDeposit(account.accountId()));
    }
}
