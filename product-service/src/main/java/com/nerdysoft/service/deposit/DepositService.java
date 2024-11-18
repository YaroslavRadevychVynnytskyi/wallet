package com.nerdysoft.service.deposit;

import com.nerdysoft.entity.deposit.Deposit;
import com.nerdysoft.model.enums.Currency;
import com.nerdysoft.model.enums.DepositStatus;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public interface DepositService {
    Deposit applyDeposit(UUID accountId, String accountEmail, BigDecimal amount, Currency walletCurrency);

    Deposit withdrawDeposit(UUID accountId);

    void deleteById(UUID depositId);

    void cancelWithdrawDeposit(UUID depositId, BigDecimal amount, LocalDate maturityDate, LocalDate notificationDate, DepositStatus depositStatus);
}
