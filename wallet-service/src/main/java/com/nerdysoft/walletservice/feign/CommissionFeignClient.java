package com.nerdysoft.walletservice.feign;

import com.nerdysoft.walletservice.config.FeignConfig;
import com.nerdysoft.walletservice.dto.feign.CalcCommissionRequestDto;
import com.nerdysoft.walletservice.dto.feign.CalcCommissionResponseDto;
import com.nerdysoft.walletservice.dto.feign.SaveCommissionRequestDto;
import com.nerdysoft.walletservice.dto.feign.SaveCommissionResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(contextId = "commissionFeignClient", value = "api-gateway", configuration = FeignConfig.class)
public interface CommissionFeignClient {
    @PostMapping("/commission-service/commissions/calc")
    ResponseEntity<CalcCommissionResponseDto> calculateCommission(@RequestBody CalcCommissionRequestDto requestDto);

    @PostMapping("/commission-service/commissions/save")
    ResponseEntity<SaveCommissionResponseDto> saveCommission(@RequestBody SaveCommissionRequestDto requestDto);
}
