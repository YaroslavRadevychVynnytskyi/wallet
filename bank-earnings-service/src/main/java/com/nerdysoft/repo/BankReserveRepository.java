package com.nerdysoft.repo;

import com.nerdysoft.entity.reserve.BankReserve;
import com.nerdysoft.entity.reserve.enums.ReserveType;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BankReserveRepository extends JpaRepository<BankReserve, UUID> {
    Optional<BankReserve> findByType(ReserveType type);
}
