package com.nerdysoft.controller;

import com.nerdysoft.dto.api.request.CalcCommissionRequestDto;
import com.nerdysoft.dto.api.response.CommissionResponseDto;
import com.nerdysoft.service.CommissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/commissions")
@RequiredArgsConstructor
public class CommissionController {
    private final CommissionService commissionService;

    @PostMapping
    public ResponseEntity<CommissionResponseDto> calculateCommission(@RequestBody CalcCommissionRequestDto requestDto) {
        return ResponseEntity.ok(commissionService.calculateCommission(requestDto));
    }
}
