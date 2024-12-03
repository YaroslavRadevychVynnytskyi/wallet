package com.nerdysoft.service;

import com.nerdysoft.axon.command.commission.SaveCommissionCommand;
import com.nerdysoft.dto.api.request.CalcCommissionRequestDto;
import com.nerdysoft.dto.commission.CalcCommissionResponseDto;
import com.nerdysoft.entity.Commission;
import java.util.UUID;

public interface CommissionService {
    CalcCommissionResponseDto calculateCommission(CalcCommissionRequestDto requestDto);

    Commission saveCommission(SaveCommissionCommand command);

    Commission findById(UUID id);

    void delete(UUID id);
}
