package com.nerdysoft.repo;

import com.nerdysoft.model.enums.ReserveType;
import com.nerdysoft.model.reserve.BankReserve;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BankReserveRepository extends JpaRepository<BankReserve, UUID> {
    Optional<BankReserve> findByType(ReserveType type);
}
