package com.nerdysoft.config;

import com.nerdysoft.dto.request.AddOrUpdateRateRequestDto;
import com.nerdysoft.model.enums.Currency;
import com.nerdysoft.service.CurrencyExchangeService;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class SchedulerConfig {
  private final CurrencyExchangeService currencyExchangeService;

  @Scheduled(cron = "0 0 0 * * *")
  private void updateExchangeRates() {
    Arrays.stream(Currency.values())
        .map(c -> currencyExchangeService.fetchExchangeRates(c.getCode()))
        .forEach(c -> currencyExchangeService.updateExchangeRate(new AddOrUpdateRateRequestDto(c.getBaseCode(), c.getConversionRates())));
  }
}
