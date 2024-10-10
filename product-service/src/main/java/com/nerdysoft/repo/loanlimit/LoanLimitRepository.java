package com.nerdysoft.repo.loanlimit;

import com.nerdysoft.entity.loanlimit.LoanLimit;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface LoanLimitRepository extends JpaRepository<LoanLimit, UUID> {
    boolean existsByAccountIdAndIsRepaidFalse(UUID accountId);

    Optional<LoanLimit> findByWalletIdAndIsRepaidFalse(UUID walletId);

    Optional<LoanLimit> findByAccountIdAndIsRepaidFalse(UUID accountId);

    @Query("SELECT ll FROM LoanLimit ll WHERE ll.dueDate < :now AND ll.isRepaid = false")
    List<LoanLimit> findOverdueLoans(LocalDateTime now);
}
