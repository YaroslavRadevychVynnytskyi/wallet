package com.nerdysoft.axon.event.deposit;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WithdrawDepositEvent {
    private UUID accountId;
}
