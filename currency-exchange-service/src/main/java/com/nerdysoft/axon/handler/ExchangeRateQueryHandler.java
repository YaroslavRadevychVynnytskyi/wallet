package com.nerdysoft.axon.handler;

import com.nerdysoft.axon.query.ConvertQuery;
import com.nerdysoft.axon.query.FindExchangeRateByBaseCodeQuery;
import com.nerdysoft.axon.query.GetExchangeRateQuery;
import com.nerdysoft.dto.request.ConvertAmountRequestDto;
import com.nerdysoft.dto.request.ExchangeRateRequestDto;
import com.nerdysoft.dto.response.ConvertAmountResponseDto;
import com.nerdysoft.dto.response.ExchangeRateResponseDto;
import com.nerdysoft.entity.ExchangeRate;
import com.nerdysoft.service.CurrencyExchangeService;
import lombok.RequiredArgsConstructor;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ExchangeRateQueryHandler {
  private final CurrencyExchangeService currencyExchangeService;

  @QueryHandler
  public ExchangeRateResponseDto handle(GetExchangeRateQuery query) {
    return currencyExchangeService.getExchangeRate(new ExchangeRateRequestDto(query.getFromCurrency(), query.getToCurrency()));
  }

  @QueryHandler
  public ConvertAmountResponseDto handle(ConvertQuery query) {
    return currencyExchangeService.convert(new ConvertAmountRequestDto(query.getFromCurrency(), query.getToCurrency(), query.getAmount()));
  }

  @QueryHandler
  public ExchangeRate handle(FindExchangeRateByBaseCodeQuery query) {
    return currencyExchangeService.fetchExchangeRates(query.getBaseCode());
  }
}
