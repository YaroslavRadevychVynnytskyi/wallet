package com.nerdysoft.service;

import com.nerdysoft.axon.command.CreateBalanceCommand;
import com.nerdysoft.axon.event.CreateBalanceEvent;
import com.nerdysoft.axon.event.UpdateBalanceEvent;
import com.nerdysoft.dto.api.request.UpdateBalanceDto;
import com.nerdysoft.entity.reserve.BankReserve;
import com.nerdysoft.entity.reserve.enums.ReserveType;
import java.math.BigDecimal;
import java.util.UUID;
import java.util.function.BiFunction;

public interface BankReserveService {
    BankReserve create(CreateBalanceCommand createBalanceCommand);

    UpdateBalanceDto updateBalance(UpdateBalanceEvent updateBalanceEvent, BiFunction<BigDecimal, BigDecimal, BigDecimal> operation);
}
