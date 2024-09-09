package com.nerdysoft.dto.feign;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import lombok.Data;

@Data
public class Wallet {
    private UUID walletId;
    private UUID accountId;
    private BigDecimal balance;
    private Currency currency;
    private LocalDate createdAt;
}
