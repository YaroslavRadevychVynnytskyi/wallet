package com.nerdysoft.controller;

import com.nerdysoft.axon.query.commission.CalculateCommissionQuery;
import com.nerdysoft.dto.api.request.CalcCommissionRequestDto;
import com.nerdysoft.dto.commission.CalcCommissionResponseDto;
import lombok.RequiredArgsConstructor;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/commissions")
@RequiredArgsConstructor
public class CommissionController {
    private final QueryGateway queryGateway;

    @PostMapping("/calc")
    public ResponseEntity<CalcCommissionResponseDto> calculateCommission(@RequestBody CalcCommissionRequestDto requestDto) {
        CalculateCommissionQuery query = new CalculateCommissionQuery(requestDto.getUsedWalletOwnAmount(), requestDto.isLoanLimitUsed(),
            requestDto.getUsedLoanLimitAmount(), requestDto.getFromWalletCurrency(), requestDto.getToWalletCurrency(),
            requestDto.getTransactionCurrency());
        return ResponseEntity.ok(queryGateway.query(query, CalcCommissionResponseDto.class).join());
    }
}
