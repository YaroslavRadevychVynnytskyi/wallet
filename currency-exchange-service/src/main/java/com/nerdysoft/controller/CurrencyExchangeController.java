package com.nerdysoft.controller;

import com.nerdysoft.axon.command.CreateExchangeRateCommand;
import com.nerdysoft.axon.command.UpdateExchangeRateCommand;
import com.nerdysoft.axon.query.ConvertQuery;
import com.nerdysoft.axon.query.FindExchangeRateByBaseCodeQuery;
import com.nerdysoft.axon.query.GetExchangeRateQuery;
import com.nerdysoft.dto.request.AddOrUpdateRateRequestDto;
import com.nerdysoft.dto.request.ConvertAmountRequestDto;
import com.nerdysoft.dto.request.ExchangeRateRequestDto;
import com.nerdysoft.dto.response.ConvertAmountResponseDto;
import com.nerdysoft.dto.response.ExchangeRateResponseDto;
import com.nerdysoft.model.ExchangeRate;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.queryhandling.QueryGateway;
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
    private final CommandGateway commandGateway;

    private final QueryGateway queryGateway;

    @Operation(summary = "Display current exchange rates")
    @GetMapping
    public ResponseEntity<ExchangeRateResponseDto> getExchangeRate(
            @RequestBody ExchangeRateRequestDto dto) {
        ExchangeRateResponseDto response = queryGateway.query(new GetExchangeRateQuery(dto.fromCurrency(), dto.toCurrency()), ExchangeRateResponseDto.class).join();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Manually add exchange rate")
    @PostMapping
    public ResponseEntity<ExchangeRate> addExchangeRate(
            @RequestBody AddOrUpdateRateRequestDto dto) {
        String baseCode = commandGateway.sendAndWait(new CreateExchangeRateCommand(dto.baseCode(), dto.conversionRates()));
        ExchangeRate rate = queryGateway.query(new FindExchangeRateByBaseCodeQuery(baseCode), ExchangeRate.class).join();
        return new ResponseEntity<>(rate, HttpStatus.CREATED);
    }

    @Operation(summary = "Manually update exchange rate")
    @PutMapping
    public ResponseEntity<ExchangeRateResponseDto> updateExchangeRate(
        @RequestBody AddOrUpdateRateRequestDto dto) {
        return new ResponseEntity<>(commandGateway.sendAndWait(new UpdateExchangeRateCommand(dto.baseCode(), dto.conversionRates())),
            HttpStatus.CREATED);
    }

    @Operation(summary = "Convert amount from one currency to another")
    @PostMapping("/convert")
    public ResponseEntity<ConvertAmountResponseDto> convert(@RequestBody ConvertAmountRequestDto dto) {
        return ResponseEntity.ok(queryGateway.query(new ConvertQuery(dto.fromCurrency(),
            dto.toCurrency(), dto.amount()), ConvertAmountResponseDto.class).join());
    }
}
