package com.nerdysoft.service.impl;

import com.nerdysoft.axon.command.CreateBalanceCommand;
import com.nerdysoft.axon.command.UpdateBalanceCommand;
import com.nerdysoft.dto.api.request.UpdateBalanceDto;
import com.nerdysoft.model.enums.ReserveType;
import com.nerdysoft.model.exception.UniqueException;
import com.nerdysoft.model.reserve.BankReserve;
import com.nerdysoft.repo.BankReserveRepository;
import com.nerdysoft.service.BankReserveService;
import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.util.function.BiFunction;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
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
    public UpdateBalanceDto updateBalance(UpdateBalanceCommand command, BiFunction<BigDecimal, BigDecimal, BigDecimal> operation) {
        BankReserve bankReserve = getByName(command.getReserveType());

        BigDecimal amount = command.getAmount();
        BigDecimal availableFunds = bankReserve.getTotalFunds();

        BigDecimal newBalance = operation.apply(availableFunds, amount);

        if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new UniqueException("Bank reserve balance is too low to perform an operation",
                HttpStatus.NOT_ACCEPTABLE);
        }

        bankReserve.setTotalFunds(newBalance);
        bankReserveRepository.save(bankReserve);

        UpdateBalanceDto responseDto = new UpdateBalanceDto();
        BeanUtils.copyProperties(command, responseDto);

        return responseDto;
    }

    private BankReserve getByName(ReserveType reserveType) {
        return bankReserveRepository.findByType(reserveType).orElseThrow(() ->
                new EntityNotFoundException("Can't find bank reserve with name: " + reserveType.name()));
    }

    @Override
    public BankReserve findById(Integer id) {
        return bankReserveRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("No bank reserve with this id"));
    }

    @Override
    public Integer getBankReserveIdByType(ReserveType type) {
        return bankReserveRepository.findByType(type).orElseThrow(() -> new EntityNotFoundException("Can't find bank reserve with this name")).getId();
    }

    @Override
    public boolean hasDbData() {
        return bankReserveRepository.count() > 0;
    }
}
