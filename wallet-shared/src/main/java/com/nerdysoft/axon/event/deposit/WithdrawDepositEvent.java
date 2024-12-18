package com.nerdysoft.axon.event.deposit;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WithdrawDepositEvent {
    private UUID id;
    private UUID accountId;
}
