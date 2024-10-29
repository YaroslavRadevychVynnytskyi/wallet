package com.nerdysoft.feign;

import com.nerdysoft.config.FeignConfig;
import com.nerdysoft.dto.feign.Transaction;
import com.nerdysoft.dto.feign.TransactionRequestDto;
import com.nerdysoft.dto.feign.TransactionResponseDto;
import com.nerdysoft.dto.feign.Wallet;
import com.nerdysoft.model.enums.Currency;
import java.util.List;
import java.util.UUID;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(contextId = "walletFeignClient", value = "api-gateway", configuration = FeignConfig.class)
public interface WalletFeignClient {
    @GetMapping("/wallet-service/wallets/account/{accountId}")
    ResponseEntity<Wallet> getWalletByAccountIdAndCurrency(@PathVariable UUID accountId,
                                                           @RequestParam Currency currency);

    @GetMapping("/wallet-service/transactions/{walletId}")
    ResponseEntity<List<Transaction>> getTransactionsByWalletId(@PathVariable UUID walletId);

    @PostMapping("/wallet-service/wallets/{walletId}/deposit")
    ResponseEntity<TransactionResponseDto> deposit(@PathVariable UUID walletId,
                                                   @RequestBody TransactionRequestDto transactionRequestDto);

    @PostMapping("/wallet-service/wallets/{walletId}/withdraw")
    ResponseEntity<TransactionResponseDto> withdraw(@PathVariable UUID walletId, @RequestBody TransactionRequestDto transactionRequestDto);
}
