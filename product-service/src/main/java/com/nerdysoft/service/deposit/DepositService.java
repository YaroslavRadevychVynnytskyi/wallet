package com.nerdysoft.service.deposit;

import com.nerdysoft.entity.deposit.Deposit;
import com.nerdysoft.model.enums.Currency;
import java.math.BigDecimal;
import java.util.UUID;

public interface DepositService {
    Deposit applyDeposit(UUID accountId, String accountEmail, BigDecimal amount, Currency walletCurrency);

    Deposit withdrawDeposit(UUID accountId);
}
