package com.nerdysoft.walletservice.controller;

import com.nerdysoft.walletservice.dto.request.CreateWalletDto;
import com.nerdysoft.walletservice.dto.request.TransactionRequestDto;
import com.nerdysoft.walletservice.dto.request.TransferRequestDto;
import com.nerdysoft.walletservice.dto.response.TransactionResponseDto;
import com.nerdysoft.walletservice.dto.response.TransferResponseDto;
import com.nerdysoft.walletservice.model.Transaction;
import com.nerdysoft.walletservice.model.Wallet;
import com.nerdysoft.walletservice.model.enums.Currency;
import com.nerdysoft.walletservice.service.WalletService;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("wallets")
@RequiredArgsConstructor
public class WalletController {
  private final WalletService walletService;

  @PostMapping
  private ResponseEntity<Wallet> createWallet(@RequestBody CreateWalletDto createWalletDto) {
    return new ResponseEntity<>(walletService.createWallet(createWalletDto), HttpStatus.CREATED);
  }

  @GetMapping("{walletId}")
  private ResponseEntity<Wallet> getWallet(@PathVariable UUID walletId) {
    return ResponseEntity.ok(walletService.getWallet(walletId));
  }

  @PatchMapping("{walletId}")
  private ResponseEntity<Wallet> updateCurrency(@PathVariable UUID walletId, @RequestParam Currency currency) {
    return new ResponseEntity<>(walletService.updateCurrency(walletId, currency), HttpStatus.ACCEPTED);
  }

  @DeleteMapping("{walletId}")
  private ResponseEntity<String> deleteWallet(@PathVariable UUID walletId) {
    return new ResponseEntity<>(walletService.deleteWallet(walletId), HttpStatus.ACCEPTED);
  }

  @PostMapping("{walletId}/deposit")
  private ResponseEntity<TransactionResponseDto> deposit(@PathVariable UUID walletId, @RequestBody TransactionRequestDto transactionRequestDto) {
    return new ResponseEntity<>(walletService.transaction(walletId, transactionRequestDto, BigDecimal::add),
        HttpStatus.ACCEPTED);
  }

  @PostMapping("{walletId}/withdraw")
  private ResponseEntity<TransactionResponseDto> withdraw(@PathVariable UUID walletId, @RequestBody TransactionRequestDto transactionRequestDto) {
    return new ResponseEntity<>(walletService.transaction(walletId, transactionRequestDto, BigDecimal::subtract),
        HttpStatus.ACCEPTED);
  }

  @PostMapping("{walletId}/transfer")
  private ResponseEntity<TransferResponseDto> transfer(@PathVariable UUID walletId, @RequestBody TransferRequestDto transferRequestDto) {
    return new ResponseEntity<>(walletService.transferToAnotherWallet(walletId, transferRequestDto), HttpStatus.ACCEPTED);
  }

  @GetMapping("account/{accountId}")
  private ResponseEntity<Wallet> getWalletByAccountIdAndCurrency(@PathVariable UUID accountId, @RequestParam Currency currency) {
    return ResponseEntity.ok(walletService.getWalletByAccountIdAndCurrency(accountId, currency));
  }
}
