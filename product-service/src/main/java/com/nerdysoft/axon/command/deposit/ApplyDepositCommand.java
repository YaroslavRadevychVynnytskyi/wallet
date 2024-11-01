package com.nerdysoft.axon.command.deposit;

import com.nerdysoft.model.enums.Currency;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplyDepositCommand {
    private UUID accountId;
    private String accountEmail;
    private BigDecimal amount;
    private Currency walletCurrency;
}
