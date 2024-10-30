package com.nerdysoft.axon.query;

import com.nerdysoft.entity.reserve.enums.ReserveType;
import lombok.Data;

@Data
public class FindBankReserveIdByTypeQuery {
    private final ReserveType reserveType;
}
