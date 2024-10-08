package com.nerdysoft.service;

import com.nerdysoft.dto.api.request.CommissionRequestMessage;

public interface CommissionService {
    void calculateCommission(CommissionRequestMessage requestMessage);
}
