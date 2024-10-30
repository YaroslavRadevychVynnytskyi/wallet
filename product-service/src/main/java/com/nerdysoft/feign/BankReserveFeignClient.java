package com.nerdysoft.feign;

import com.nerdysoft.config.FeignConfig;
import com.nerdysoft.dto.feign.BankReserveOperationsDto;
import com.nerdysoft.dto.feign.BankReserveTypeDto;
import com.nerdysoft.dto.feign.UpdateBalanceDto;
import java.util.UUID;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(contextId = "bankReserveFeignClient", value = "api-gateway", configuration = FeignConfig.class)
public interface BankReserveFeignClient {
    @PostMapping("/bank-earnings-service/reserves/update-balance")
    ResponseEntity<UpdateBalanceDto> updateBalance(@RequestBody UpdateBalanceDto requestDto);

    @PostMapping("/bank-earnings-service/reserves/by-type")
    ResponseEntity<UUID> getReserveIdByType(@RequestBody BankReserveTypeDto reserveTypeDto);
}
