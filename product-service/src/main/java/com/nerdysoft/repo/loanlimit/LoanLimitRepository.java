package com.nerdysoft.repo.loanlimit;

import com.nerdysoft.entity.loanlimit.LoanLimit;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoanLimitRepository extends JpaRepository<LoanLimit, UUID> {
    boolean existsByAccountId(UUID accountId);

    Optional<LoanLimit> findByWalletId(UUID walletId);
}
