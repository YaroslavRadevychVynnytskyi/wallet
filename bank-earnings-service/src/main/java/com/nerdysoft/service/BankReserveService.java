package com.nerdysoft.service;

import com.nerdysoft.axon.command.CreateBalanceCommand;
import com.nerdysoft.axon.command.bankreserve.UpdateBalanceCommand;
import com.nerdysoft.dto.api.response.UpdateBalanceResponseDto;
import com.nerdysoft.model.enums.ReserveType;
import com.nerdysoft.model.reserve.BankReserve;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiFunction;

public interface BankReserveService {
    BankReserve create(CreateBalanceCommand createBalanceCommand);

    UpdateBalanceResponseDto updateBalance(UpdateBalanceCommand command, BiFunction<BigDecimal, BigDecimal, BigDecimal> operation);

    Optional<BankReserve> getByName(ReserveType reserveType);

    UUID getBankReserveIdByType(ReserveType type);

    boolean hasAllReserveTypesStored();

    BankReserve findByReserveType(ReserveType reserveType);

    void receiveCommission(ReserveType reserveType, BigDecimal commission);
}
