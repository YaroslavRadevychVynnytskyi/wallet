package com.nerdysoft.axon.event.deposit;

import com.nerdysoft.model.enums.DepositStatus;
import java.util.UUID;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DepositApprovedEvent {
    private UUID id;
    private DepositStatus depositStatus = DepositStatus.FROZEN;
}
