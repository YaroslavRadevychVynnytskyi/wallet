package com.nerdysoft.repo;

import com.nerdysoft.entity.Role;
import com.nerdysoft.model.enums.RoleName;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
  Optional<Role> findByName(RoleName name);
}
