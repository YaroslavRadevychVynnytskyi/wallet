package com.nerdysoft.service;

import com.nerdysoft.dto.request.AddOrUpdateRateRequestDto;
import com.nerdysoft.dto.request.ConvertAmountRequestDto;
import com.nerdysoft.dto.request.ExchangeRateRequestDto;
import com.nerdysoft.dto.response.ConvertAmountResponseDto;
import com.nerdysoft.dto.response.ExchangeRateResponseDto;
import com.nerdysoft.entity.ExchangeRate;
import java.util.Optional;

public interface CurrencyExchangeService {
    ExchangeRateResponseDto getExchangeRate(ExchangeRateRequestDto requestDto);

    ExchangeRate addExchangeRate(AddOrUpdateRateRequestDto dto);

    ExchangeRate updateExchangeRate(AddOrUpdateRateRequestDto dto);

    ConvertAmountResponseDto convert(ConvertAmountRequestDto requestDto);

    ExchangeRate fetchExchangeRates(String baseCode);

    Optional<ExchangeRate> findByBaseCode(String baseCode);

    boolean allCurrenciesStored();
}
