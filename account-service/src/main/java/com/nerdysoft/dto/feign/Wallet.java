package com.nerdysoft.dto.feign;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Wallet {
    private UUID walletId;
    private UUID accountId;
    private BigDecimal balance;
    private Currency currency;
    private LocalDateTime createdAt;
}
