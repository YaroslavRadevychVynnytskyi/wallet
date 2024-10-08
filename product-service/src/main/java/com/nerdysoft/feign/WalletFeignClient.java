package com.nerdysoft.feign;

import com.nerdysoft.config.FeignConfig;
import com.nerdysoft.dto.feign.Transaction;
import com.nerdysoft.dto.feign.Wallet;
import com.nerdysoft.dto.feign.enums.Currency;
import java.util.List;
import java.util.UUID;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(contextId = "walletFeignClient", value = "api-gateway", configuration = FeignConfig.class)
public interface WalletFeignClient {
    @GetMapping("/wallet-service/wallets/account/{accountId}")
    ResponseEntity<Wallet> getWalletByAccountIdAndCurrency(@PathVariable UUID accountId,
                                                           @RequestParam Currency currency);

    @GetMapping("/wallet-service/transactions/{walletId}")
    ResponseEntity<List<Transaction>> getTransactionsByWalletId(@PathVariable UUID walletId);
}
