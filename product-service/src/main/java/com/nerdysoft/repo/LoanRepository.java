package com.nerdysoft.repo;

import com.nerdysoft.dto.api.request.loan.enums.PaymentType;
import com.nerdysoft.entity.loan.Loan;
import com.nerdysoft.entity.loan.enums.ApprovalStatus;
import com.nerdysoft.entity.loan.enums.RepaymentStatus;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoanRepository extends JpaRepository<Loan, UUID> {
    boolean existsByAccountIdAndApprovalStatusAndRepaymentStatus(UUID accountId, ApprovalStatus approvalStatus, RepaymentStatus repaymentStatus);

    List<Loan> findAllByApprovalStatusAndPaymentTypeAndRepaymentStatusAndNextPayment(ApprovalStatus approvalStatus, PaymentType paymentType, RepaymentStatus repaymentStatus, LocalDate date);

    Optional<Loan> findByAccountIdAndRepaymentStatus(UUID accountId, RepaymentStatus status);
}
