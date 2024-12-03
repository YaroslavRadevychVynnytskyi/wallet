package com.nerdysoft.service.impl;

import com.nerdysoft.axon.command.CreateBalanceCommand;
import com.nerdysoft.axon.command.bankreserve.UpdateBalanceCommand;
import com.nerdysoft.dto.api.response.UpdateBalanceResponseDto;
import com.nerdysoft.model.enums.ReserveType;
import com.nerdysoft.model.exception.UniqueException;
import com.nerdysoft.model.reserve.BankReserve;
import com.nerdysoft.repo.BankReserveRepository;
import com.nerdysoft.service.BankReserveService;
import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
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
    public UpdateBalanceResponseDto updateBalance(UpdateBalanceCommand command, BiFunction<BigDecimal, BigDecimal, BigDecimal> operation) {
        BankReserve bankReserve = getByName(command.getReserveType()).orElseThrow(() ->
            new EntityNotFoundException("Can't find bank reserve with name: " + command.getReserveType().name()));

        BigDecimal amount = command.getAmount();
        BigDecimal availableFunds = bankReserve.getTotalFunds();

        BigDecimal newBalance = operation.apply(availableFunds, amount);

        if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new UniqueException("Bank reserve balance is too low to perform an operation",
                HttpStatus.NOT_ACCEPTABLE);
        }

        bankReserve.setTotalFunds(newBalance);
        bankReserveRepository.save(bankReserve);

        UpdateBalanceResponseDto responseDto = new UpdateBalanceResponseDto();
        BeanUtils.copyProperties(command, responseDto);

        responseDto.setBalance(newBalance);

        return responseDto;
    }

    @Override
    public Optional<BankReserve> getByName(ReserveType reserveType) {
        return bankReserveRepository.findByType(reserveType);
    }

    @Override
    public UUID getBankReserveIdByType(ReserveType type) {
        return bankReserveRepository.findByType(type).orElseThrow(() -> new EntityNotFoundException("Can't find bank reserve with this name")).getId();
    }

    @Override
    public boolean hasAllReserveTypesStored() {
        return bankReserveRepository.count() == ReserveType.values().length;
    }

    @Override
    public BankReserve findByReserveType(ReserveType reserveType) {
        return bankReserveRepository.findByType(reserveType).orElseThrow(() ->
                new EntityNotFoundException("Can't find bank reserve of type" + reserveType.name()));
    }

    @Override
    public void receiveCommission(ReserveType reserveType, BigDecimal commission) {
        BankReserve bankReserve = findByReserveType(reserveType);

        bankReserve.setTotalFunds(bankReserve.getTotalFunds().add(commission));

        bankReserveRepository.save(bankReserve);
    }
}
