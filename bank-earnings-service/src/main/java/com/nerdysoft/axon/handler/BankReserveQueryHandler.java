package com.nerdysoft.axon.handler;

import com.nerdysoft.axon.query.FindBankReserveByIdQuery;
import com.nerdysoft.axon.query.FindBankReserveIdByTypeQuery;
import com.nerdysoft.model.reserve.BankReserve;
import com.nerdysoft.service.BankReserveService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BankReserveQueryHandler {
    private final BankReserveService bankReserveService;

    @QueryHandler
    public UUID findBankReserveIdByType(FindBankReserveIdByTypeQuery query) {
        return bankReserveService.getBankReserveIdByType(query.getReserveType());
    }

    @QueryHandler
    public BankReserve handle(FindBankReserveByIdQuery query) {
        return bankReserveService.findById(query.getId());
    }
}
