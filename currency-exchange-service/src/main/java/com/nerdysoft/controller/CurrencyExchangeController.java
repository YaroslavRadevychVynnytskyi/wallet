package com.nerdysoft.controller;

import com.nerdysoft.dto.request.AddOrUpdateRateRequestDto;
import com.nerdysoft.dto.request.ConvertAmountRequestDto;
import com.nerdysoft.dto.request.ExchangeRateRequestDto;
import com.nerdysoft.dto.response.ConvertAmountResponseDto;
import com.nerdysoft.dto.response.ExchangeRateResponseDto;
import com.nerdysoft.entity.ExchangeRate;
import com.nerdysoft.service.CurrencyExchangeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
    public ResponseEntity<ExchangeRateResponseDto> getExchangeRate(
            @RequestBody ExchangeRateRequestDto dto) {
        return ResponseEntity.ok(currencyExchangeService.getExchangeRate(dto));
    }

    @Operation(summary = "Manually add exchange rate")
    @PostMapping
    public ResponseEntity<ExchangeRate> addExchangeRate(
            @RequestBody AddOrUpdateRateRequestDto dto) {
        return new ResponseEntity<>(currencyExchangeService.addExchangeRate(dto), HttpStatus.CREATED);
    }

    @Operation(summary = "Manually update exchange rate")
    @PutMapping
    public ResponseEntity<ExchangeRate> updateExchangeRate(
        @RequestBody AddOrUpdateRateRequestDto dto) {
        return new ResponseEntity<>(currencyExchangeService.updateExchangeRate(dto), HttpStatus.ACCEPTED);
    }

    @Operation(summary = "Convert amount from one currency to another")
    @PostMapping("/convert")
    public ResponseEntity<ConvertAmountResponseDto> convert(@RequestBody ConvertAmountRequestDto dto) {
        return ResponseEntity.ok(currencyExchangeService.convert(dto));
    }
}
