package com.nerdysoft.repo.deposit;

import com.nerdysoft.entity.deposit.Deposit;
import com.nerdysoft.model.enums.DepositStatus;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface DepositRepository extends JpaRepository<Deposit, UUID> {
    List<Deposit> findByMaturityDateEquals(LocalDate now);

    boolean existsByAccountIdAndDepositStatusNot(UUID accountId, DepositStatus depositStatus);

    @Query("SELECT d FROM Deposit d WHERE d.notificationDate <= :tenDaysAgo AND d.depositStatus = :depositStatus")
    List<Deposit> findDepositsToFreeze(LocalDate tenDaysAgo, DepositStatus depositStatus);

    Optional<Deposit> findByAccountIdAndDepositStatus(UUID accountId, DepositStatus depositStatus);
}
