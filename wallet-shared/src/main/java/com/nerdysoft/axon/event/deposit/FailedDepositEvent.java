package com.nerdysoft.axon.event.deposit;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FailedDepositEvent {
    private UUID id;
    private UUID accountId;
    private String reason;
    private LocalDateTime timestamp;
}
