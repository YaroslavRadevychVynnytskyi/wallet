package com.nerdysoft.controller;

import com.nerdysoft.axon.command.CreateBalanceCommand;
import com.nerdysoft.axon.command.bankreserve.UpdateBalanceCommand;
import com.nerdysoft.axon.query.FindBankReserveByTypeQuery;
import com.nerdysoft.axon.query.FindBankReserveIdByTypeQuery;
import com.nerdysoft.dto.api.request.BankReserveTypeDto;
import com.nerdysoft.dto.api.request.CreateBalanceDto;
import com.nerdysoft.dto.api.request.UpdateBalanceRequestDto;
import com.nerdysoft.dto.api.response.UpdateBalanceResponseDto;
import com.nerdysoft.model.enums.ReserveType;
import com.nerdysoft.model.reserve.BankReserve;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.beans.BeanUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/reserves")
@RequiredArgsConstructor
public class BankReserveController {
    private final CommandGateway commandGateway;
    private final QueryGateway queryGateway;

    @PostMapping("/create")
    public ResponseEntity<BankReserve> create(@RequestBody CreateBalanceDto requestDto) {
        CreateBalanceCommand createBalanceCommand = new CreateBalanceCommand();
        BeanUtils.copyProperties(requestDto, createBalanceCommand);

        ReserveType reserveType = commandGateway.sendAndWait(createBalanceCommand);
        BankReserve bankReserve = queryGateway.query(new FindBankReserveByTypeQuery(reserveType), BankReserve.class).join();
        return ResponseEntity.ok(bankReserve);
    }

    @PostMapping("/update-balance")
    public ResponseEntity<UpdateBalanceResponseDto> updateBalance(@RequestBody UpdateBalanceRequestDto requestDto) {
        UpdateBalanceCommand updateBalanceCommand = new UpdateBalanceCommand();
        BeanUtils.copyProperties(requestDto, updateBalanceCommand);

        return ResponseEntity.ok(commandGateway.sendAndWait(updateBalanceCommand));
    }

    @PostMapping("/by-type")
    public ResponseEntity<UUID> getReserveIdByType(@RequestBody BankReserveTypeDto reserveTypeDto) {
        FindBankReserveIdByTypeQuery findBankReserveIdByTypeQuery = new FindBankReserveIdByTypeQuery(reserveTypeDto.reserveType());

        return ResponseEntity.ok(queryGateway.query(findBankReserveIdByTypeQuery, UUID.class).join());
    }
}
