package com.nerdysoft.service.deposit;

import com.nerdysoft.dto.api.request.deposit.DepositRequestDto;
import com.nerdysoft.entity.deposit.Deposit;
import java.util.UUID;

public interface DepositService {
    Deposit applyDeposit(UUID accountId, String accountEmail, DepositRequestDto requestDto);

    Deposit withdrawDeposit(UUID accountId);
}
