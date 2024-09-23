package com.nerdysoft.runner;

import com.nerdysoft.service.CurrencyExchangeService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StartupRunner implements CommandLineRunner {
  private final CurrencyExchangeService currencyExchangeService;

  @Override
  public void run(String... args) {
    if (!currencyExchangeService.hasDbData()) {
      currencyExchangeService.updateExchangeRates();
    }
  }
}
