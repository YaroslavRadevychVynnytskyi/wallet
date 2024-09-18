package com.nerdysoft.service.impl;

import com.nerdysoft.entity.Role;
import com.nerdysoft.entity.enums.RoleName;
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
  public Role getRoleById(Integer id) {
    return roleRepository.findById(id).orElseThrow(EntityNotFoundException::new);
  }

  @Override
  public Role getRoleByName(RoleName name) {
    return roleRepository.findByName(name).orElseThrow(EntityNotFoundException::new);
  }

  @Override
  public Role create(RoleName name) {
    return roleRepository.save(new Role(null, name));
  }

  @Override
  public Role update(RoleName name) {
    Role role = getRoleByName(name);
    role.setName(role.getName());
    return roleRepository.save(role);
  }

  @Override
  public String delete(Integer id) {
    roleRepository.findById(id);
    roleRepository.deleteById(id);
    return String.format("Role with id %d was deleted", id);
  }
}
