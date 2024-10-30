package com.nerdysoft.service.impl;

import com.nerdysoft.axon.command.CreateBalanceCommand;
import com.nerdysoft.axon.event.UpdateBalanceEvent;
import com.nerdysoft.dto.api.request.UpdateBalanceDto;
import com.nerdysoft.entity.reserve.BankReserve;
import com.nerdysoft.entity.reserve.enums.ReserveType;
import com.nerdysoft.repo.BankReserveRepository;
import com.nerdysoft.service.BankReserveService;
import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.util.function.BiFunction;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BankReserveServiceImpl implements BankReserveService {
    private final BankReserveRepository bankReserveRepository;

    @Override
    public BankReserve create(CreateBalanceCommand createBalanceCommand) {
        BankReserve bankReserve = new BankReserve();
        bankReserve.setType(createBalanceCommand.getReserveType());
        bankReserve.setTotalFunds(createBalanceCommand.getAmount());

        return bankReserveRepository.save(bankReserve);
    }

    @Override
    public UpdateBalanceDto updateBalance(UpdateBalanceEvent updateBalanceEvent, BiFunction<BigDecimal, BigDecimal, BigDecimal> operation) {
        BankReserve bankReserve = getByName(updateBalanceEvent.getReserveType());

        BigDecimal amount = updateBalanceEvent.getAmount();
        BigDecimal availableFunds = bankReserve.getTotalFunds();

        BigDecimal newBalance = operation.apply(availableFunds, amount);

        if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalStateException("Bank reserve balance is too low to perform an operation");
        }

        bankReserve.setTotalFunds(newBalance);
        bankReserveRepository.save(bankReserve);

        UpdateBalanceDto responseDto = new UpdateBalanceDto();
        BeanUtils.copyProperties(updateBalanceEvent, responseDto);

        return responseDto;
    }

    private BankReserve getByName(ReserveType reserveType) {
        return bankReserveRepository.findByType(reserveType).orElseThrow(() ->
                new EntityNotFoundException("Can't find bank reserve with name: " + reserveType.name()));
    }
}
