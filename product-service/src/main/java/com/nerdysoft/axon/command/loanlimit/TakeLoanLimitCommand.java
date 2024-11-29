package com.nerdysoft.axon.command.loanlimit;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TakeLoanLimitCommand {
    private UUID accountId;

    private String email;
}
