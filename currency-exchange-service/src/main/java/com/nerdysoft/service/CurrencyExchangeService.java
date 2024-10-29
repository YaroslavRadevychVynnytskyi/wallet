package com.nerdysoft.service;

import com.nerdysoft.dto.request.AddOrUpdateRateRequestDto;
import com.nerdysoft.dto.request.ConvertAmountRequestDto;
import com.nerdysoft.dto.request.ExchangeRateRequestDto;
import com.nerdysoft.dto.response.ConvertAmountResponseDto;
import com.nerdysoft.dto.response.ExchangeRateResponseDto;
import com.nerdysoft.entity.ExchangeRate;
import java.math.BigDecimal;
import java.util.Map;

public interface CurrencyExchangeService {
    ExchangeRate findByBaseCode(String baseCode);

    ExchangeRateResponseDto getExchangeRate(ExchangeRateRequestDto requestDto);

    ExchangeRate addExchangeRate(AddOrUpdateRateRequestDto dto);

    ExchangeRate updateExchangeRate(String baseCode, Map<String, BigDecimal> updatedConversionRates);

    ConvertAmountResponseDto convert(ConvertAmountRequestDto requestDto);

    ExchangeRate fetchExchangeRates(String baseCode);

    boolean hasDbData();
}
