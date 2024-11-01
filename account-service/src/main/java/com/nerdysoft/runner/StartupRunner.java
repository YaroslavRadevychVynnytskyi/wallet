package com.nerdysoft.runner;

import com.nerdysoft.axon.command.role.CreateRoleCommand;
import com.nerdysoft.model.enums.RoleName;
import com.nerdysoft.service.RoleService;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StartupRunner implements CommandLineRunner {
  private final RoleService roleService;

  private final CommandGateway commandGateway;

  @Override
  public void run(String... args) {
    if (!roleService.hasDbData()) {
      Arrays.stream(RoleName.values())
          .forEach(name -> commandGateway.send(new CreateRoleCommand(name)));
    }
  }
}
