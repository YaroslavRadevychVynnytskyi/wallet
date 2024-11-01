package com.nerdysoft.service;

import com.nerdysoft.axon.command.CreateBalanceCommand;
import com.nerdysoft.axon.command.UpdateBalanceCommand;
import com.nerdysoft.dto.api.request.UpdateBalanceDto;
import com.nerdysoft.model.enums.ReserveType;
import com.nerdysoft.model.reserve.BankReserve;
import java.math.BigDecimal;
import java.util.UUID;
import java.util.function.BiFunction;

public interface BankReserveService {
    BankReserve create(CreateBalanceCommand createBalanceCommand);

    UpdateBalanceDto updateBalance(UpdateBalanceCommand command, BiFunction<BigDecimal, BigDecimal, BigDecimal> operation);

    BankReserve findById(UUID id);

    UUID getBankReserveIdByType(ReserveType type);

    boolean hasDbData();
}
