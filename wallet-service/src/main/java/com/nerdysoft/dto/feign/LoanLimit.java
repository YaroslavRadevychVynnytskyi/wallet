package com.nerdysoft.dto.feign;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Data;

@Data
public class LoanLimit {
    private UUID id;
    private UUID accountId;
    private BigDecimal availableAmount;
    private LocalDateTime timestamp;
}
