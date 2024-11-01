package com.nerdysoft.runner;

import com.nerdysoft.axon.command.CreateBalanceCommand;
import com.nerdysoft.model.enums.ReserveType;
import com.nerdysoft.service.BankReserveService;
import java.math.BigDecimal;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StartupRunner implements CommandLineRunner {
  private final BankReserveService bankReserveService;

  private final CommandGateway commandGateway;

  @Override
  public void run(String... args) {
    if (!bankReserveService.hasDbData()) {
      Arrays.stream(ReserveType.values())
          .forEach(type -> commandGateway.sendAndWait(new CreateBalanceCommand(type, BigDecimal.ZERO)));
    }
  }
}
