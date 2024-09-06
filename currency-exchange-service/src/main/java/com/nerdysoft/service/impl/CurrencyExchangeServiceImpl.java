package com.nerdysoft.service.impl;

import com.nerdysoft.dto.AddOrUpdateRateRequestDto;
import com.nerdysoft.dto.AddOrUpdateRateResponseDto;
import com.nerdysoft.dto.CurrencyExchangeRatesDto;
import com.nerdysoft.dto.ExchangeRateRequestDto;
import com.nerdysoft.dto.ExchangeRateResponseDto;
import com.nerdysoft.entity.ExchangeRate;
import com.nerdysoft.mapper.ExchangeRateMapper;
import com.nerdysoft.repo.ExchangeRateRepository;
import com.nerdysoft.service.CurrencyExchangeService;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
public class CurrencyExchangeServiceImpl implements CurrencyExchangeService {
    private final ExchangeRateRepository exchangeRateRepository;
    private final ExchangeRateMapper exchangeRateMapper;

    private final WebClient webClient;

    @Value("${external.api.exchange.url}")
    private String exchangeApiUrl;

    @Override
    public ExchangeRateResponseDto getExchangeRate(ExchangeRateRequestDto requestDto) {
        Optional<ExchangeRate> cachedExchangeRate = exchangeRateRepository.findByFromCurrencyAndToCurrency(requestDto.fromCurrency(), requestDto.toCurrency());
        if (cachedExchangeRate.isPresent()) {
            return exchangeRateMapper.toDto(cachedExchangeRate.get());
        }

        ExchangeRate fetchedExchangeRate = fetchExchangeRate(requestDto.fromCurrency(), requestDto.toCurrency());
        ExchangeRate savedExchangeRate = exchangeRateRepository.save(fetchedExchangeRate);

        return exchangeRateMapper.toDto(savedExchangeRate);
    }

    @Override
    public AddOrUpdateRateResponseDto addOrUpdateExchangeRate(AddOrUpdateRateRequestDto requestDto) {
        Optional<ExchangeRate> cachedExchangeRate = exchangeRateRepository.findByFromCurrencyAndToCurrency(requestDto.fromCurrency(), requestDto.toCurrency());

        LocalDateTime now = LocalDateTime.now();

        ExchangeRate exchangeRate = cachedExchangeRate
                .map(r -> {
                    r.setExchangeRate(requestDto.exchangeRate());
                    r.setTimestamp(now);
                    return r;
                })
                .orElseGet(() -> {
                    ExchangeRate newRate = exchangeRateMapper.toModel(requestDto);
                    newRate.setTimestamp(now);
                    return newRate;
                });

        exchangeRateRepository.save(exchangeRate);
        return new AddOrUpdateRateResponseDto("success", now);
    }

    private ExchangeRate fetchExchangeRate(String fromCurrency, String toCurrency) {
        CurrencyExchangeRatesDto exchangeRates = webClient.get()
                .uri(exchangeApiUrl + fromCurrency)
                .retrieve()
                .bodyToMono(CurrencyExchangeRatesDto.class)
                .block();
        Double rate = exchangeRates.conversionRates().get(toCurrency);

        return ExchangeRate.builder()
                .fromCurrency(fromCurrency)
                .toCurrency(toCurrency)
                .exchangeRate(BigDecimal.valueOf(rate))
                .timestamp(LocalDateTime.now())
                .build();
    }
}
