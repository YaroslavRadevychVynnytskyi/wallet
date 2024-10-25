package com.nerdysoft.service.impl;

import com.nerdysoft.dto.generic.ConversionRatesDto;
import com.nerdysoft.dto.request.AddOrUpdateRateRequestDto;
import com.nerdysoft.dto.request.ConvertAmountRequestDto;
import com.nerdysoft.dto.request.ExchangeRateRequestDto;
import com.nerdysoft.dto.response.ConvertAmountResponseDto;
import com.nerdysoft.dto.response.ExchangeRateResponseDto;
import com.nerdysoft.model.ExchangeRate;
import com.nerdysoft.repo.ExchangeRateRepository;
import com.nerdysoft.service.CurrencyExchangeService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
public class CurrencyExchangeServiceImpl implements CurrencyExchangeService {
    private final ExchangeRateRepository exchangeRateRepository;
    private final WebClient webClient;

    @Value("${external.api.exchange.url}")
    private String exchangeApiUrl;

    @Override
    public ExchangeRate findByBaseCode(String baseCode) {
        return exchangeRateRepository.findByBaseCode(baseCode)
            .orElseThrow(() -> new NoSuchElementException(String.format("No currency with base code: %s", baseCode)));
    }

    @Override
    public ExchangeRateResponseDto getExchangeRate(ExchangeRateRequestDto requestDto) {
        ExchangeRate rate = getExchangeRate(requestDto.fromCurrency(), requestDto.toCurrency());
        return new ExchangeRateResponseDto(rate, requestDto.toCurrency());
    }

    private ExchangeRate getExchangeRate(String fromCurrency, String toCurrency) {
        LocalDate today = LocalDate.now();
        return exchangeRateRepository.findByFromCurrencyAndToCurrency(
                fromCurrency,
                toCurrency,
                today.atStartOfDay(),
                today.atStartOfDay().plusDays(1)
                ).orElseThrow(() -> new NoSuchElementException("Can't find such exchange rate"));
    }

    @Override
    public ExchangeRate addExchangeRate(AddOrUpdateRateRequestDto dto) {
        return exchangeRateRepository.save(new ExchangeRate(dto.baseCode(), dto.conversionRates(), LocalDateTime.now()));
    }

    @Override
    public ExchangeRate updateExchangeRate(String baseCode, Map<String, BigDecimal> updatedConversionRates) {
        ExchangeRate rate = findByBaseCode(baseCode);
        rate.setConversionRates(updatedConversionRates);
        rate.setTimestamp(LocalDateTime.now());
        return exchangeRateRepository.save(rate);
    }

    @Override
    public ConvertAmountResponseDto convert(ConvertAmountRequestDto requestDto) {
        ExchangeRate exchangeRate = getExchangeRate(requestDto.fromCurrency(), requestDto.toCurrency());

        BigDecimal rateValue = exchangeRate.getConversionRates().get(requestDto.toCurrency());
        BigDecimal convertedAmount = requestDto.amount().multiply(rateValue);

        return new ConvertAmountResponseDto(requestDto, rateValue, convertedAmount);
    }

    @Override
    public ExchangeRate fetchExchangeRates(String baseCode) {
        ConversionRatesDto rates = webClient.get()
                .uri(exchangeApiUrl + baseCode)
                .retrieve()
                .bodyToMono(ConversionRatesDto.class)
                .block();

        return ExchangeRate.builder()
                .baseCode(baseCode)
                .conversionRates(rates.conversionRates())
                .timestamp(LocalDateTime.now())
                .build();
    }

    @Override
    public boolean hasDbData() {
        return exchangeRateRepository.count() > 0;
    }
}
