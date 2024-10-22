package com.nerdysoft.axon.command.role;

import com.nerdysoft.model.enums.RoleName;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Getter
@RequiredArgsConstructor
public class UpdateRoleCommand {
  @TargetAggregateIdentifier
  private final Integer roleId;

  private final RoleName name;
}
