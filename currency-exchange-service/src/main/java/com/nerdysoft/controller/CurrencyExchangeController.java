package com.nerdysoft.controller;

import com.nerdysoft.dto.AddOrUpdateRateRequestDto;
import com.nerdysoft.dto.AddOrUpdateRateResponseDto;
import com.nerdysoft.dto.ExchangeRateRequestDto;
import com.nerdysoft.dto.ExchangeRateResponseDto;
import com.nerdysoft.service.CurrencyExchangeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Currency exchange management", description = "Endpoints for managing currency rates")
@RestController
@RequestMapping("/exchange-rate")
@RequiredArgsConstructor
public class CurrencyExchangeController {
    private final CurrencyExchangeService currencyExchangeService;

    @Operation(summary = "Display current exchange rates")
    @GetMapping
    public ResponseEntity<ExchangeRateResponseDto> getExchangeRate(@RequestBody ExchangeRateRequestDto requestDto) {
        return ResponseEntity.ok(currencyExchangeService.getExchangeRate(requestDto));
    }

    @Operation(summary = "Manually add or update exchange rate")
    @PostMapping
    public ResponseEntity<AddOrUpdateRateResponseDto> addOrUpdateExchangeRate(@RequestBody AddOrUpdateRateRequestDto requestDto) {
        return ResponseEntity.ok(currencyExchangeService.addOrUpdateExchangeRate(requestDto));
    }
}
