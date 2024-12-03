package com.nerdysoft.dto.loanlimit;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Data;

@Data
public class LoanLimitDto {
    private UUID id;
    private UUID accountId;
    private BigDecimal availableAmount;
    private LocalDateTime timestamp;
}
