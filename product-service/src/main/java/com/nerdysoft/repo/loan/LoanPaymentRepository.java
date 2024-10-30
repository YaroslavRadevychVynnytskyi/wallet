package com.nerdysoft.repo.loan;

import com.nerdysoft.entity.loan.LoanPayment;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoanPaymentRepository extends JpaRepository<LoanPayment, UUID> {
    boolean existsByAccountIdAndTimestampBetween(UUID accountId, LocalDateTime startOfMonth, LocalDateTime endOfMonth);

    Optional<LoanPayment> findByLoanId(UUID loanId);
}
