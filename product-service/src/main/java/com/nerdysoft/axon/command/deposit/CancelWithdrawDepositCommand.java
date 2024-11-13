package com.nerdysoft.axon.command.deposit;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CancelWithdrawDepositCommand {
    private UUID id;
}
