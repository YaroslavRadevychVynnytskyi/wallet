package com.nerdysoft.axon.handler;

import com.nerdysoft.axon.query.CalculateCommissionQuery;
import com.nerdysoft.axon.query.FindCommissionByIdQuery;
import com.nerdysoft.dto.api.request.CalcCommissionRequestDto;
import com.nerdysoft.dto.api.response.CalcCommissionResponseDto;
import com.nerdysoft.model.Commission;
import com.nerdysoft.service.CommissionService;
import lombok.RequiredArgsConstructor;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommissionQueryHandler {
  private final CommissionService commissionService;

  @QueryHandler
  public Commission handle(FindCommissionByIdQuery query) {
    return commissionService.findById(query.getId());
  }

  @QueryHandler
  public CalcCommissionResponseDto handle(CalculateCommissionQuery query) {
    CalcCommissionRequestDto dto = new CalcCommissionRequestDto(query.getWalletAmount(), query.isLoanLimitUsed(),
        query.getLoanLimitAmount(), query.getFromWalletCurrency(), query.getToWalletCurrency(),
        query.getTransactionCurrency());
    return commissionService.calculateCommission(dto);
  }
}
