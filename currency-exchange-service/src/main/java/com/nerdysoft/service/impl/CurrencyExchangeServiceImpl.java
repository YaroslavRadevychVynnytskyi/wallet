package com.nerdysoft.service.impl;

import com.nerdysoft.dto.generic.ConversionRatesDto;
import com.nerdysoft.dto.request.AddOrUpdateRateRequestDto;
import com.nerdysoft.dto.request.ConvertAmountRequestDto;
import com.nerdysoft.dto.request.ExchangeRateRequestDto;
import com.nerdysoft.dto.response.AddOrUpdateRateResponseDto;
import com.nerdysoft.dto.response.ConvertAmountResponseDto;
import com.nerdysoft.dto.response.ExchangeRateResponseDto;
import com.nerdysoft.model.ExchangeRate;
import com.nerdysoft.model.enums.Currency;
import com.nerdysoft.repo.ExchangeRateRepository;
import com.nerdysoft.service.CurrencyExchangeService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
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
    public AddOrUpdateRateResponseDto addOrUpdateExchangeRate(AddOrUpdateRateRequestDto requestDto) {
        ExchangeRate rate = exchangeRateRepository
                .findByBaseCode(requestDto.fromCurrency())
                .orElseThrow();

        LocalDateTime now = LocalDateTime.now();

        rate.getConversionRates().put(requestDto.toCurrency(), requestDto.exchangeRate());
        rate.setTimestamp(now);

        exchangeRateRepository.save(rate);
        return new AddOrUpdateRateResponseDto("success", now);
    }

    @Override
    public ConvertAmountResponseDto convert(ConvertAmountRequestDto requestDto) {
        ExchangeRate exchangeRate = getExchangeRate(requestDto.fromCurrency(), requestDto.toCurrency());

        BigDecimal rateValue = exchangeRate.getConversionRates().get(requestDto.toCurrency());
        BigDecimal convertedAmount = requestDto.amount().multiply(rateValue);

        return new ConvertAmountResponseDto(requestDto, rateValue, convertedAmount);
    }

    private ExchangeRate fetchExchangeRates(String baseCode) {
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
    @Scheduled(cron = "0 0 0 * * *")
    public void updateExchangeRates() {
        List<ExchangeRate> actualRates = Arrays.stream(Currency.values())
                .map(c -> fetchExchangeRates(c.getCode()))
                .toList();
        exchangeRateRepository.saveAll(actualRates);
    }

    @Override
    public boolean hasDbData() {
        return exchangeRateRepository.count() > 0;
    }
}
