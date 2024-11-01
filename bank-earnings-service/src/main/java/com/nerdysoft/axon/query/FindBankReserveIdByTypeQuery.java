package com.nerdysoft.axon.query;

import com.nerdysoft.model.enums.ReserveType;
import lombok.Data;

@Data
public class FindBankReserveIdByTypeQuery {
    private final ReserveType reserveType;
}
