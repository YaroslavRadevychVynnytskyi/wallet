package com.nerdysoft.repo;

import com.nerdysoft.entity.loan.LoanPayment;
import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoanPaymentRepository extends JpaRepository<LoanPayment, UUID> {
    boolean existsByAccountIdAndTimestampBetween(UUID accountId, LocalDateTime startOfMonth, LocalDateTime endOfMonth);
}
