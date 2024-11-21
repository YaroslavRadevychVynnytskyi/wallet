package com.nerdysoft.service.impl;

import com.nerdysoft.dto.generic.ConversionRatesDto;
import com.nerdysoft.dto.request.AddOrUpdateRateRequestDto;
import com.nerdysoft.dto.request.ConvertAmountRequestDto;
import com.nerdysoft.dto.request.ExchangeRateRequestDto;
import com.nerdysoft.dto.response.ConvertAmountResponseDto;
import com.nerdysoft.dto.response.ExchangeRateResponseDto;
import com.nerdysoft.entity.ExchangeRate;
import com.nerdysoft.model.enums.Currency;
import com.nerdysoft.model.exception.UniqueException;
import com.nerdysoft.repo.ExchangeRateRepository;
import com.nerdysoft.service.CurrencyExchangeService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
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
    public ExchangeRate updateExchangeRate(AddOrUpdateRateRequestDto dto) {
        Optional<ExchangeRate> rate = exchangeRateRepository.findByBaseCode(dto.baseCode());
        if (rate.isPresent()) {
            rate.get().setConversionRates(dto.conversionRates());
            rate.get().setTimestamp(LocalDateTime.now());
            return exchangeRateRepository.save(rate.get());
        } else {
            return addExchangeRate(dto);
        }
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
        Optional<ConversionRatesDto> rates = Optional.ofNullable(webClient.get()
                .uri(exchangeApiUrl + baseCode)
                .retrieve()
                .bodyToMono(ConversionRatesDto.class)
                .block());

        if (rates.isPresent()) {
            return ExchangeRate.builder()
                .baseCode(baseCode)
                .conversionRates(rates.get().conversionRates())
                .timestamp(LocalDateTime.now())
                .build();
        } else {
            throw new UniqueException(String.format("%s not found", baseCode), HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public Optional<ExchangeRate> findByBaseCode(String baseCode) {
        return exchangeRateRepository.findByBaseCode(baseCode);
    }

    @Override
    public boolean allCurrenciesStored() {
        return exchangeRateRepository.count() == Currency.values().length;
    }
}
