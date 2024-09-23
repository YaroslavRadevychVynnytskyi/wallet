package com.nerdysoft.service;

import com.nerdysoft.dto.request.AddOrUpdateRateRequestDto;
import com.nerdysoft.dto.request.ConvertAmountRequestDto;
import com.nerdysoft.dto.request.ExchangeRateRequestDto;
import com.nerdysoft.dto.response.AddOrUpdateRateResponseDto;
import com.nerdysoft.dto.response.ConvertAmountResponseDto;
import com.nerdysoft.dto.response.ExchangeRateResponseDto;

public interface CurrencyExchangeService {
    ExchangeRateResponseDto getExchangeRate(ExchangeRateRequestDto requestDto);

    AddOrUpdateRateResponseDto addOrUpdateExchangeRate(AddOrUpdateRateRequestDto requestDto);

    ConvertAmountResponseDto convert(ConvertAmountRequestDto requestDto);

    void updateExchangeRates();

    boolean hasDbData();
}
