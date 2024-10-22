package com.nerdysoft.service.impl;

import com.nerdysoft.model.Role;
import com.nerdysoft.model.enums.RoleName;
import com.nerdysoft.repo.RoleRepository;
import com.nerdysoft.service.RoleService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {
  private final RoleRepository roleRepository;

  @Override
  public Role findById(Integer id) {
    return roleRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException(String.format("Role with id %d was not found", id)));
  }

  @Override
  public Role findByName(RoleName name) {
    return roleRepository.findByName(name)
        .orElseThrow(() -> new EntityNotFoundException(String.format("Role with name %s was not found", name)));
  }

  @Override
  public Role create(RoleName name) {
    return roleRepository.save(new Role(null, name));
  }

  @Override
  public Role update(Integer id, RoleName name) {
    Role role = findById(id);
    role.setName(name);
    return roleRepository.save(role);
  }

  @Override
  public String delete(Integer id) {
    roleRepository.findById(id);
    roleRepository.deleteById(id);
    return String.format("Role with id %d was deleted", id);
  }
}
