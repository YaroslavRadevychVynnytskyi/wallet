package com.nerdysoft.service;

import com.nerdysoft.dto.api.request.BankReserveOperationsDto;
import com.nerdysoft.entity.reserve.BankReserve;

public interface BankReserveService {
    BankReserve create(BankReserveOperationsDto requestDto);

    BankReserveOperationsDto withdraw(BankReserveOperationsDto requestDto);
}
