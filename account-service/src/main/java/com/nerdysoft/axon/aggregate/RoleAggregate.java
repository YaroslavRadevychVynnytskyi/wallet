package com.nerdysoft.axon.aggregate;

import com.nerdysoft.axon.command.role.CreateRoleCommand;
import com.nerdysoft.axon.command.role.DeleteRoleCommand;
import com.nerdysoft.axon.command.role.UpdateRoleCommand;
import com.nerdysoft.axon.event.role.RoleCreatedEvent;
import com.nerdysoft.axon.event.role.RoleDeletedEvent;
import com.nerdysoft.axon.event.role.RoleUpdatedEvent;
import com.nerdysoft.entity.Role;
import com.nerdysoft.model.enums.RoleName;
import com.nerdysoft.service.RoleService;
import lombok.NoArgsConstructor;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;

@Aggregate
@NoArgsConstructor
public class RoleAggregate {
  @AggregateIdentifier
  private Integer id;

  private RoleName name;

  @CommandHandler
  public RoleAggregate(CreateRoleCommand command, RoleService roleService) {
    Role role = roleService.create(command.getName());
    AggregateLifecycle.apply(new RoleCreatedEvent(role.getId(), role.getName()));
  }

  @EventSourcingHandler
  public void on(RoleCreatedEvent event) {
    id = event.getRoleId();
    name = event.getName();
  }

  @CommandHandler
  public Role handle(UpdateRoleCommand command, RoleService roleService) {
    Role role = roleService.update(command.getRoleId(), command.getName());
    AggregateLifecycle.apply(new RoleUpdatedEvent(role.getId(), role.getName()));
    return role;
  }

  @EventSourcingHandler
  public void on(RoleUpdatedEvent event) {
    name = event.getName();
  }

  @CommandHandler
  public String handle(DeleteRoleCommand command, RoleService roleService) {
    String message = roleService.delete(command.getRoleId());
    AggregateLifecycle.apply(new RoleDeletedEvent(command.getRoleId()));
    return message;
  }

  @EventSourcingHandler
  public void on(RoleDeletedEvent event) {
    AggregateLifecycle.markDeleted();
  }
}
