package com.nerdysoft.controller.loanlimit;

import com.nerdysoft.dto.api.request.LoanLimitRequestDto;
import com.nerdysoft.entity.loanlimit.LoanLimit;
import com.nerdysoft.service.loanlimit.LoanLimitService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/loan-limits")
@RequiredArgsConstructor
public class LoanLimitController {
    private final LoanLimitService loanLimitService;

    @GetMapping
    public ResponseEntity<LoanLimit> getLoanLimit(@RequestBody LoanLimitRequestDto requestDto) {
        return ResponseEntity.ok(loanLimitService.getLoanLimit(requestDto));
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
