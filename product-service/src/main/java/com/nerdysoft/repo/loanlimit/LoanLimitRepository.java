package com.nerdysoft.repo.loanlimit;

import com.nerdysoft.entity.loanlimit.LoanLimit;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface LoanLimitRepository extends JpaRepository<LoanLimit, UUID> {
    @Query("SELECT COUNT(ll) > 0 FROM LoanLimit ll WHERE ll.accountId = :accountId AND ll.repaid = FALSE")
    boolean checkIfExistsNotRepaidLoanLimitByAccountId(UUID accountId);

//    @Query("SELECT ll FROM LoanLimit ll WHERE ll.walletId = :walletId AND ll.repaid = FALSE")
//    Optional<LoanLimit> findNotRepaidLoanLimitByWalletId(UUID walletId);

    @Query("SELECT ll FROM LoanLimit ll WHERE ll.accountId = :accountId AND ll.repaid = FALSE")
    Optional<LoanLimit> findNotRepaidLoanLimitByAccountId(UUID accountId);

    @Query("SELECT ll FROM LoanLimit ll WHERE ll.dueDate < :now AND ll.repaid = FALSE")
    List<LoanLimit> findAllNotRepaidLoanLimitsByDueDate(LocalDateTime now);
}
