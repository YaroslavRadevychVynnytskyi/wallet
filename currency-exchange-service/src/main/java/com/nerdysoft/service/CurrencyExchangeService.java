package com.nerdysoft.service;

import com.nerdysoft.dto.AddOrUpdateRateRequestDto;
import com.nerdysoft.dto.AddOrUpdateRateResponseDto;
import com.nerdysoft.dto.ExchangeRateRequestDto;
import com.nerdysoft.dto.ExchangeRateResponseDto;

public interface CurrencyExchangeService {
    ExchangeRateResponseDto getExchangeRate(ExchangeRateRequestDto requestDto);

    AddOrUpdateRateResponseDto addOrUpdateExchangeRate(AddOrUpdateRateRequestDto requestDto);
}
