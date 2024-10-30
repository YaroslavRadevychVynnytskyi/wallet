package com.nerdysoft.axon.aggregate;

import com.nerdysoft.axon.command.CreateExchangeRateCommand;
import com.nerdysoft.axon.command.UpdateExchangeRateCommand;
import com.nerdysoft.axon.event.exchangeRate.ExchangeRateCreatedEvent;
import com.nerdysoft.axon.event.exchangeRate.ExchangeRateUpdatedEvent;
import com.nerdysoft.dto.request.AddOrUpdateRateRequestDto;
import com.nerdysoft.entity.ExchangeRate;
import com.nerdysoft.service.CurrencyExchangeService;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import lombok.NoArgsConstructor;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;

@Aggregate
@NoArgsConstructor
public class ExchangeRateAggregate {
  @AggregateIdentifier
  private String baseCode;

  private Map<String, BigDecimal> conversionRates;

  private LocalDateTime timestamp;

  @CommandHandler
  public ExchangeRateAggregate(CreateExchangeRateCommand command, CurrencyExchangeService currencyExchangeService) {
    ExchangeRate rate = currencyExchangeService.addExchangeRate(new AddOrUpdateRateRequestDto(command.getBaseCode(), command.getConversionRates()));
    AggregateLifecycle.apply(new ExchangeRateCreatedEvent(rate.getBaseCode(), rate.getConversionRates(), rate.getTimestamp()));
  }

  @EventSourcingHandler
  public void on(ExchangeRateCreatedEvent event) {
    conversionRates = event.getConversionRates();
    timestamp = event.getTimestamp();
  }

  @CommandHandler
  public ExchangeRate handle(UpdateExchangeRateCommand command, CurrencyExchangeService currencyExchangeService) {
    ExchangeRate rate = currencyExchangeService.updateExchangeRate(command.getBaseCode(), command.getConversionRates());
    AggregateLifecycle.apply(new ExchangeRateUpdatedEvent(rate.getBaseCode(), rate.getConversionRates(), rate.getTimestamp()));
    return rate;
  }

  @EventSourcingHandler
  public void on(ExchangeRateUpdatedEvent event) {
    conversionRates = event.getConversionRates();
    timestamp = event.getTimestamp();
  }
}
