package com.nerdysoft.axon.command;

import com.nerdysoft.model.enums.ReserveType;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateBalanceCommand {
    private ReserveType reserveType;
    private BigDecimal amount;
}
