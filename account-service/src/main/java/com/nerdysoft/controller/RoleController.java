package com.nerdysoft.controller;

import com.nerdysoft.entity.Role;
import com.nerdysoft.model.enums.RoleName;
import com.nerdysoft.service.RoleService;
import lombok.RequiredArgsConstructor;
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
  private final RoleService roleService;

  @GetMapping("/{id}")
  public ResponseEntity<Role> findById(@PathVariable Integer id) {
    return ResponseEntity.ok(roleService.findById(id));
  }

  @GetMapping
  public ResponseEntity<Role> findByName(@RequestParam RoleName name) {
    return ResponseEntity.ok(roleService.findByName(name));
  }

  @PostMapping
  public ResponseEntity<Role> create(@RequestParam RoleName name) {
    return new ResponseEntity<>(roleService.create(name), HttpStatus.CREATED);
  }

  @PutMapping("/{id}")
  public ResponseEntity<Role> update(@PathVariable Integer id, @RequestParam RoleName name) {
    return new ResponseEntity<>(roleService.update(id, name), HttpStatus.ACCEPTED);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<String> delete(@PathVariable Integer id) {
    return new ResponseEntity<>(roleService.delete(id), HttpStatus.ACCEPTED);
  }
}
