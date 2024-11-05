package com.nerdysoft.axon.aggregate;

import com.nerdysoft.axon.command.SaveCommissionCommand;
import com.nerdysoft.axon.event.commission.CommissionSavedEvent;
import com.nerdysoft.dto.api.request.SaveCommissionRequestDto;
import com.nerdysoft.entity.Commission;
import com.nerdysoft.service.CommissionService;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.NoArgsConstructor;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;

@Aggregate
@NoArgsConstructor
public class CommissionAggregate {
  @AggregateIdentifier
  private UUID commissionId;

  private UUID transactionId;

  private BigDecimal walletAmount;

  private boolean isLoanLimitUsed;

  private BigDecimal loanLimitAmount;

  private BigDecimal usdCommissionAmount;

  private BigDecimal senderCurrencyCommissionAmount;

  private String fromWalletCurrency;

  private String toWalletCurrency;

  private String transactionCurrency;

  @CommandHandler
  public CommissionAggregate(SaveCommissionCommand command, CommissionService commissionService) {
    SaveCommissionRequestDto dto = new SaveCommissionRequestDto(command.getTransactionId(), command.getUsdCommission(),
        command.getOriginalCurrencyCommission(), command.getWalletAmount(), command.isLoanLimitUsed(),
        command.getLoanLimitAmount(), command.getFromWalletCurrency(), command.getToWalletCurrency(),
        command.getTransactionCurrency());
    Commission commission = commissionService.saveCommission(dto);
    AggregateLifecycle.apply(new CommissionSavedEvent(commission.getCommissionId(), commission.getTransactionId(),
        commission.getWalletAmount(), commission.isLoanLimitUsed(), commission.getLoanLimitAmount(),
        commission.getUsdCommissionAmount(), commission.getSenderCurrencyCommissionAmount(),
        commission.getFromWalletCurrency(), commission.getToWalletCurrency(), commission.getTransactionCurrency()
        ));
  }

  @EventSourcingHandler
  public void on(CommissionSavedEvent event) {
    commissionId = event.getCommissionId();
    transactionId = event.getTransactionId();
    walletAmount = event.getWalletAmount();
    isLoanLimitUsed = event.isLoanLimitUsed();
    loanLimitAmount = event.getLoanLimitAmount();
    usdCommissionAmount = event.getUsdCommissionAmount();
    senderCurrencyCommissionAmount = event.getSenderCurrencyCommissionAmount();
    fromWalletCurrency = event.getFromWalletCurrency();
    toWalletCurrency = event.getToWalletCurrency();
    transactionCurrency = event.getTransactionCurrency();
  }
}
