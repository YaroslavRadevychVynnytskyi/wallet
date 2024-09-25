package com.nerdysoft.feign;

import com.nerdysoft.dto.feign.CalcCommissionRequestDto;
import com.nerdysoft.dto.feign.CommissionResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(contextId = "commissionFeignClient", value = "api-gateway")
public interface CommissionFeignClient {
    @PostMapping("/commission-service/commissions")
    ResponseEntity<CommissionResponseDto> calculateCommission(@RequestBody CalcCommissionRequestDto requestDto);
}
