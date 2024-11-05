package com.nerdysoft.runner;

import com.nerdysoft.dto.request.AddOrUpdateRateRequestDto;
import com.nerdysoft.model.enums.Currency;
import com.nerdysoft.service.CurrencyExchangeService;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StartupRunner implements CommandLineRunner {
  private final CurrencyExchangeService currencyExchangeService;

  @Override
  public void run(String... args) {
    if (!currencyExchangeService.allCurrenciesStored()) {
      Arrays.stream(Currency.values())
          .filter(c -> currencyExchangeService.findByBaseCode(c.getCode()).isEmpty())
          .map(c -> currencyExchangeService.fetchExchangeRates(c.getCode()))
          .forEach(c -> currencyExchangeService.addExchangeRate(new AddOrUpdateRateRequestDto(c.getBaseCode(), c.getConversionRates())));
    }
  }
}
