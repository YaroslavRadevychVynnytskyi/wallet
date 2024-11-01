package com.nerdysoft.repo;

import com.nerdysoft.entity.Commission;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommissionRepository extends JpaRepository<Commission, UUID> {
}
