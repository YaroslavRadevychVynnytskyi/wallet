package com.nerdysoft.service.feign;

import com.nerdysoft.config.FeignConfig;
import com.nerdysoft.dto.feign.BankReserveOperationsDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(contextId = "bankReserveFeignClient", value = "api-gateway", configuration = FeignConfig.class)
public interface BankReserveFeignClient {
    @PostMapping("/bank-earnings-service/reserves/withdraw")
    ResponseEntity<BankReserveOperationsDto> withdraw(@RequestBody BankReserveOperationsDto requestDto);

    @PostMapping("/bank-earnings-service/reserves/deposit")
    ResponseEntity<BankReserveOperationsDto> deposit(@RequestBody BankReserveOperationsDto requestDto);
}
