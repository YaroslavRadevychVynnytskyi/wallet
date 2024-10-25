package com.nerdysoft.runner;

import com.nerdysoft.axon.command.CreateExchangeRateCommand;
import com.nerdysoft.model.enums.Currency;
import com.nerdysoft.service.CurrencyExchangeService;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StartupRunner implements CommandLineRunner {
  private final CurrencyExchangeService currencyExchangeService;

  private final CommandGateway commandGateway;

  @Override
  public void run(String... args) {
    if (!currencyExchangeService.hasDbData()) {
      Arrays.stream(Currency.values())
          .map(c -> currencyExchangeService.fetchExchangeRates(c.getCode()))
          .forEach(c -> commandGateway.send(new CreateExchangeRateCommand(c.getBaseCode(), c.getConversionRates())));
    }
  }
}
