package com.nerdysoft.service;

import com.nerdysoft.dto.api.request.CalcCommissionRequestDto;
import com.nerdysoft.dto.api.request.SaveCommissionRequestDto;
import com.nerdysoft.dto.api.response.CalcCommissionResponseDto;
import com.nerdysoft.entity.Commission;
import java.util.UUID;

public interface CommissionService {
    CalcCommissionResponseDto calculateCommission(CalcCommissionRequestDto requestDto);

    Commission saveCommission(SaveCommissionRequestDto requestDto);

    Commission findById(UUID id);
}
