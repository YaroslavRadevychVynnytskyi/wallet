package com.nerdysoft.service;

import com.nerdysoft.dto.api.request.CalcCommissionRequestDto;
import com.nerdysoft.dto.api.response.CommissionResponseDto;

public interface CommissionService {
    CommissionResponseDto calculateCommission(CalcCommissionRequestDto requestDto);
}
