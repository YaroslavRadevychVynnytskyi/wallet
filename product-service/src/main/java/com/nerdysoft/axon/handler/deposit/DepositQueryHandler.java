package com.nerdysoft.axon.handler.deposit;

import com.nerdysoft.axon.query.FindDepositByIdQuery;
import com.nerdysoft.entity.deposit.Deposit;
import com.nerdysoft.repo.deposit.DepositRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DepositQueryHandler {
    private final DepositRepository depositRepository;

    @QueryHandler
    public Deposit findDepositById(FindDepositByIdQuery query) {
        return depositRepository.findById(query.getDepositId()).orElseThrow(() ->
                new EntityNotFoundException("Can't find a deposit with ID: " + query.getDepositId()));
    }
}
