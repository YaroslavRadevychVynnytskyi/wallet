package com.nerdysoft.axon.query;

import com.nerdysoft.model.enums.ReserveType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class FindBankReserveByTypeQuery {
    private ReserveType reserveType;
}
