package com.nerdysoft.controller;

import com.nerdysoft.dto.api.request.BankReserveOperationsDto;
import com.nerdysoft.entity.reserve.BankReserve;
import com.nerdysoft.service.BankReserveService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/reserves")
@RequiredArgsConstructor
public class BankReserveController {
    private final BankReserveService bankReserveService;

    @PostMapping("/create")
    public ResponseEntity<BankReserve> create(@RequestBody BankReserveOperationsDto requestDto) {
        return ResponseEntity.ok(bankReserveService.create(requestDto));
    }

    @PostMapping("/withdraw")
    public ResponseEntity<BankReserveOperationsDto> withdraw(@RequestBody BankReserveOperationsDto requestDto) {
        return ResponseEntity.ok(bankReserveService.withdraw(requestDto));
    }
}
