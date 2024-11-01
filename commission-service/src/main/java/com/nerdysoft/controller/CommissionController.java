package com.nerdysoft.controller;

import com.nerdysoft.axon.command.SaveCommissionCommand;
import com.nerdysoft.axon.query.CalculateCommissionQuery;
import com.nerdysoft.axon.query.FindCommissionByIdQuery;
import com.nerdysoft.dto.api.request.CalcCommissionRequestDto;
import com.nerdysoft.dto.api.request.SaveCommissionRequestDto;
import com.nerdysoft.dto.api.response.CalcCommissionResponseDto;
import com.nerdysoft.dto.api.response.SaveCommissionResponseDto;
import com.nerdysoft.mapper.CommissionMapper;
import com.nerdysoft.entity.Commission;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/commissions")
@RequiredArgsConstructor
public class CommissionController {
    private final CommandGateway commandGateway;

    private final QueryGateway queryGateway;

    private final CommissionMapper commissionMapper;

    @PostMapping("/calc")
    public ResponseEntity<CalcCommissionResponseDto> calculateCommission(@RequestBody CalcCommissionRequestDto requestDto) {
        CalculateCommissionQuery query = new CalculateCommissionQuery(requestDto.getWalletAmount(), requestDto.isLoanLimitUsed(),
            requestDto.getLoanLimitAmount(), requestDto.getFromWalletCurrency(), requestDto.getToWalletCurrency(),
            requestDto.getTransactionCurrency());
        return ResponseEntity.ok(queryGateway.query(query, CalcCommissionResponseDto.class).join());
    }

    @PostMapping("/save")
    public ResponseEntity<SaveCommissionResponseDto> saveCommission(@RequestBody SaveCommissionRequestDto requestDto) {
        SaveCommissionCommand command = new SaveCommissionCommand(requestDto.getTransactionId(), requestDto.getUsdCommission(),
            requestDto.getOriginalCurrencyCommission(), requestDto.getWalletAmount(), requestDto.isLoanLimitUsed(),
            requestDto.getLoanLimitAmount(), requestDto.getFromWalletCurrency(), requestDto.getToWalletCurrency(),
            requestDto.getTransactionCurrency());
        UUID commissionId = commandGateway.sendAndWait(command);
        Commission commission = queryGateway.query(new FindCommissionByIdQuery(commissionId), Commission.class).join();
        return ResponseEntity.ok(commissionMapper.toResponseDto(commission));
    }
}
