package com.nerdysoft.axon.command.role;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DeleteRoleCommand {
  @TargetAggregateIdentifier
  private Integer roleId;
}
