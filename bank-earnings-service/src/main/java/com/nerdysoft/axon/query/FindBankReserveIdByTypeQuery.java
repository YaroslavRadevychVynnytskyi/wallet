package com.nerdysoft.axon.query;

import com.nerdysoft.model.enums.ReserveType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FindBankReserveIdByTypeQuery {
    private ReserveType reserveType;
}
