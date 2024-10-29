package com.nerdysoft.service.impl;

import com.nerdysoft.dto.api.request.BankReserveOperationsDto;
import com.nerdysoft.entity.reserve.BankReserve;
import com.nerdysoft.entity.reserve.enums.ReserveType;
import com.nerdysoft.repo.BankReserveRepository;
import com.nerdysoft.service.BankReserveService;
import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BankReserveServiceImpl implements BankReserveService {
    private final BankReserveRepository bankReserveRepository;

    @Override
    public BankReserve create(BankReserveOperationsDto requestDto) {
        BankReserve bankReserve = new BankReserve();
        bankReserve.setType(requestDto.getReserveType());
        bankReserve.setTotalFunds(BigDecimal.ZERO);

        return bankReserveRepository.save(bankReserve);
    }

    @Transactional
    @Override
    public BankReserveOperationsDto withdraw(BankReserveOperationsDto requestDto) {
        BankReserve bankReserve = getByName(requestDto.getReserveType());

        BigDecimal withdrawAmount = requestDto.getAmount();
        BigDecimal availableFunds = bankReserve.getTotalFunds();

        BigDecimal remainingAmount = availableFunds.subtract(withdrawAmount);
        bankReserve.setTotalFunds(remainingAmount);

        bankReserveRepository.save(bankReserve);

        return new BankReserveOperationsDto(bankReserve.getType(), withdrawAmount);
    }

    private BankReserve getByName(ReserveType reserveType) {
        return bankReserveRepository.findByType(reserveType).orElseThrow(() ->
                new EntityNotFoundException("Can't find bank reserve with name: " + reserveType.name()));
    }
}
