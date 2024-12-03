package com.nerdysoft.axon.event.deposit;

import com.nerdysoft.model.enums.DepositStatus;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CancelWithdrawDepositEvent {
    private UUID id;
    private BigDecimal amount;
    private LocalDate maturityDate;
    private LocalDate notificationDate;
    private DepositStatus depositStatus;
}
