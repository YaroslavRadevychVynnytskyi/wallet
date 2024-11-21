package com.nerdysoft.feign;

import com.nerdysoft.config.FeignConfig;
import com.nerdysoft.dto.loanlimit.LoanLimitDto;
import java.util.UUID;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(contextId = "loanLimitFeignClient", value = "api-gateway", configuration = FeignConfig.class)
public interface LoanLimitFeignClient {
    @GetMapping("/product-service/loan-limits/{walletId}")
    ResponseEntity<LoanLimitDto> getLoanLimitByWalletId(@PathVariable UUID walletId);

    @PutMapping("/product-service/loan-limits/{walletId}")
    ResponseEntity<LoanLimitDto> updateByWalletId(@PathVariable UUID walletId, @RequestBody LoanLimitDto loanLimitDto);
}
