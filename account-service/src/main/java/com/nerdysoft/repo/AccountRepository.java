package com.nerdysoft.repo;

import com.nerdysoft.entity.Account;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, UUID> {
  @EntityGraph(attributePaths = {"roles"})
  Optional<Account> findByEmail(String email);
}
