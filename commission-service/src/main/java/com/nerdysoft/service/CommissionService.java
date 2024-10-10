package com.nerdysoft.service;

import com.nerdysoft.dto.api.request.CalcCommissionRequestDto;
import com.nerdysoft.dto.api.request.SaveCommissionRequestDto;
import com.nerdysoft.dto.api.response.CalcCommissionResponseDto;
import com.nerdysoft.dto.api.response.SaveCommissionResponseDto;

public interface CommissionService {
    CalcCommissionResponseDto calculateCommission(CalcCommissionRequestDto requestDto);

    SaveCommissionResponseDto saveCommission(SaveCommissionRequestDto requestDto);
}
