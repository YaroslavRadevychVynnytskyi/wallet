package com.nerdysoft.axon.command.loan;

import com.nerdysoft.model.enums.Currency;
import com.nerdysoft.model.enums.PaymentType;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.Data;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Data
public class ApplyLoanCommand {
    @TargetAggregateIdentifier
    private UUID id;
    private UUID accountId;
    private String accountEmail;
    private BigDecimal requestedAmount;
    private Currency currency;
    private PaymentType paymentType;
}
