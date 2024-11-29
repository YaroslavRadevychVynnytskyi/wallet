package com.nerdysoft.axon.event.loanlimit;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoanLimitTookEvent {
    private UUID id;

    private UUID accountId;

    private String email;

    private BigDecimal availableAmount;

    private BigDecimal initialAmount;

    private boolean repaid;

    private LocalDateTime dueDate;
}
