package com.nerdysoft.config;

import com.nerdysoft.axon.command.UpdateExchangeRateCommand;
import com.nerdysoft.model.enums.Currency;
import com.nerdysoft.service.CurrencyExchangeService;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class SchedulerConfig {
  private final CommandGateway commandGateway;

  private final CurrencyExchangeService currencyExchangeService;

  @Scheduled(cron = "0 0 0 * * *")
  private void updateExchangeRates() {
    Arrays.stream(Currency.values())
        .map(c -> currencyExchangeService.fetchExchangeRates(c.getCode()))
        .forEach(c -> commandGateway.send(new UpdateExchangeRateCommand(c.getBaseCode(), c.getConversionRates())));
  }
}
