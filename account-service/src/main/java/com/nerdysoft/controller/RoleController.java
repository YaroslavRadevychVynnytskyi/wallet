package com.nerdysoft.controller;

import com.nerdysoft.axon.command.role.CreateRoleCommand;
import com.nerdysoft.axon.command.role.DeleteRoleCommand;
import com.nerdysoft.axon.command.role.UpdateRoleCommand;
import com.nerdysoft.axon.query.role.FindRoleByIdQuery;
import com.nerdysoft.axon.query.role.FindRoleByNameQuery;
import com.nerdysoft.model.Role;
import com.nerdysoft.model.enums.RoleName;
import lombok.RequiredArgsConstructor;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
public class RoleController {
  private final CommandGateway commandGateway;

  private final QueryGateway queryGateway;

  @GetMapping("/{id}")
  public ResponseEntity<Role> findById(@PathVariable Integer id) {
    return ResponseEntity.ok(queryGateway.query(new FindRoleByIdQuery(id), Role.class).join());
  }

  @GetMapping
  public ResponseEntity<Role> findByName(@RequestParam RoleName name) {
    return ResponseEntity.ok(queryGateway.query(new FindRoleByNameQuery(name), Role.class).join());
  }

  @PostMapping
  public ResponseEntity<Role> create(@RequestParam RoleName name) {
    Integer roleId = commandGateway.sendAndWait(new CreateRoleCommand(name));
    return new ResponseEntity<>(queryGateway.query(new FindRoleByIdQuery(roleId), Role.class).join(), HttpStatus.CREATED);
  }

  @PutMapping("/{id}")
  public ResponseEntity<Role> update(@PathVariable Integer id, @RequestParam RoleName name) {
    return new ResponseEntity<>(commandGateway.sendAndWait(new UpdateRoleCommand(id, name)), HttpStatus.ACCEPTED);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<String> delete(@PathVariable Integer id) {
    return new ResponseEntity<>(commandGateway.sendAndWait(new DeleteRoleCommand(id)), HttpStatus.ACCEPTED);
  }
}
