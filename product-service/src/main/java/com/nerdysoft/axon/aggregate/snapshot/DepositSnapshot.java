package com.nerdysoft.axon.aggregate.snapshot;

import com.nerdysoft.model.enums.DepositStatus;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DepositSnapshot {
    private BigDecimal amount;
    private LocalDate maturityDate;
    private LocalDate notificationDate;
    private DepositStatus depositStatus;
}

