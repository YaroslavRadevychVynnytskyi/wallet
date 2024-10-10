package com.nerdysoft.feign;

import com.nerdysoft.config.FeignConfig;
import com.nerdysoft.dto.feign.CreateWalletDto;
import com.nerdysoft.dto.feign.Currency;
import com.nerdysoft.dto.feign.Transaction;
import com.nerdysoft.dto.feign.TransferRequestDto;
import com.nerdysoft.dto.feign.Wallet;
import com.nerdysoft.dto.feign.WalletTransactionRequestDto;
import com.nerdysoft.dto.feign.WalletTransactionResponseDto;
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
    @PostMapping("/wallet-service/wallets")
    ResponseEntity<Wallet> createWallet(@RequestBody CreateWalletDto createWalletDto);

    @PostMapping("/wallet-service/wallets/{walletId}/transfer")
    ResponseEntity<Transaction> transfer(@PathVariable UUID walletId,
                                         @RequestBody TransferRequestDto transferRequestDto);

    @GetMapping("/wallet-service/wallets/account/{accountId}")
    ResponseEntity<Wallet> getWalletByAccountIdAndCurrency(@PathVariable UUID accountId,
                                                           @RequestParam Currency currency);

    @PostMapping("/wallet-service/wallets/{walletId}/deposit")
    ResponseEntity<WalletTransactionResponseDto> deposit(@PathVariable UUID walletId, @RequestBody WalletTransactionRequestDto transactionRequestDto);

    @PostMapping("/wallet-service/wallets/{walletId}/withdraw")
    ResponseEntity<WalletTransactionResponseDto> withdraw(@PathVariable UUID walletId, @RequestBody WalletTransactionRequestDto transactionRequestDto);
}
