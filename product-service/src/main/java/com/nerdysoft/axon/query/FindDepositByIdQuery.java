package com.nerdysoft.axon.query;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FindDepositByIdQuery {
    private final UUID depositId;
}
