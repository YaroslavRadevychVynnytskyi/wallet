package com.nerdysoft.axon.handler;

import com.nerdysoft.axon.query.FindBankReserveIdByTypeQuery;
import com.nerdysoft.repo.BankReserveRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BankReserveQueryHandler {
    private final BankReserveRepository bankReserveRepository;

    @QueryHandler
    public UUID findBankReserveIdByType(FindBankReserveIdByTypeQuery query) {
        return bankReserveRepository.findByType(query.getReserveType()).orElseThrow().getId();
    }
}
