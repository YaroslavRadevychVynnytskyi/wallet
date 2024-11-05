package com.nerdysoft.feign;

import com.nerdysoft.config.FeignConfig;
import com.nerdysoft.entity.Account;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(contextId = "accountFeignClient", value = "api-gateway", configuration = FeignConfig.class)
public interface AccountFeignClient {
  @GetMapping("/account-service/accounts/email/{email}")
  ResponseEntity<Account> getAccountByEmail(@PathVariable String email);
}
