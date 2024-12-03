package com.nerdysoft.controller;

import com.nerdysoft.annotation.BasicInfoController;
import com.nerdysoft.axon.command.wallet.CreateWalletCommand;
import com.nerdysoft.axon.command.wallet.DeleteWalletCommand;
import com.nerdysoft.axon.command.wallet.DepositToWalletCommand;
import com.nerdysoft.axon.command.wallet.TransferToAnotherWalletCommand;
import com.nerdysoft.axon.command.wallet.UpdateWalletCurrencyCommand;
import com.nerdysoft.axon.command.wallet.WithdrawFromWalletCommand;
import com.nerdysoft.axon.query.wallet.FindWalletByAccountIdAndCurrencyQuery;
import com.nerdysoft.axon.query.wallet.FindWalletByIdQuery;
import com.nerdysoft.dto.request.CreateWalletDto;
import com.nerdysoft.dto.request.DepositRequestDto;
import com.nerdysoft.dto.request.TransferRequestDto;
import com.nerdysoft.dto.response.DepositResponseDto;
import com.nerdysoft.dto.response.TransferResponseDto;
import com.nerdysoft.dto.response.WithdrawResponseDto;
import com.nerdysoft.dto.wallet.WalletDto;
import com.nerdysoft.entity.Wallet;
import com.nerdysoft.model.enums.Currency;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.queryhandling.QueryGateway;
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
@BasicInfoController(basicField = "balance", basicFieldType = "java.math.BigDecimal", pagination = true, databaseType = BasicInfoController.DatabaseType.POSTGRES)
public class WalletController {
  private final CommandGateway commandGateway;

  private final QueryGateway queryGateway;

  @PostMapping
  private ResponseEntity<Wallet> createWallet(@RequestBody CreateWalletDto createWalletDto) {
    UUID walletId = commandGateway.sendAndWait(new CreateWalletCommand(createWalletDto.accountId(), createWalletDto.currency()));
    Wallet wallet = queryGateway.query(new FindWalletByIdQuery(walletId), Wallet.class).join();
    return new ResponseEntity<>(wallet, HttpStatus.CREATED);
  }

  @GetMapping("{walletId}")
  private ResponseEntity<Wallet> findById(@PathVariable UUID walletId) {
    return ResponseEntity.ok(queryGateway.query(new FindWalletByIdQuery(walletId), Wallet.class).join());
  }

  @PatchMapping("{walletId}")
  private ResponseEntity<Wallet> updateCurrency(@PathVariable UUID walletId, @RequestParam Currency currency) {
    return new ResponseEntity<>(commandGateway.sendAndWait(new UpdateWalletCurrencyCommand(walletId, currency)), HttpStatus.ACCEPTED);
  }

  @DeleteMapping("{walletId}")
  private ResponseEntity<String> deleteWallet(@PathVariable UUID walletId) {
    return new ResponseEntity<>(commandGateway.sendAndWait(new DeleteWalletCommand(walletId)), HttpStatus.ACCEPTED);
  }

  @PostMapping("{walletId}/deposit")
  private ResponseEntity<DepositResponseDto> deposit(@PathVariable UUID walletId, @RequestBody DepositRequestDto dto) {
    return new ResponseEntity<>((DepositResponseDto) commandGateway.sendAndWait(new DepositToWalletCommand(walletId, dto.getAmount(), dto.getCurrency())), HttpStatus.ACCEPTED);
  }

  @PostMapping("{walletId}/withdraw")
  private ResponseEntity<WithdrawResponseDto> withdraw(@PathVariable UUID walletId, @RequestBody DepositRequestDto dto) {
    return new ResponseEntity<>((WithdrawResponseDto) commandGateway.sendAndWait(new WithdrawFromWalletCommand(walletId, dto.getAmount(), dto.getCurrency())), HttpStatus.ACCEPTED);
  }

  @PostMapping("{walletId}/transfer")
  private ResponseEntity<TransferResponseDto> transfer(@PathVariable UUID walletId, @RequestBody TransferRequestDto dto) {
    TransferToAnotherWalletCommand command = new TransferToAnotherWalletCommand(walletId, dto.getToWalletId(),
        dto.getAmount(), dto.getCurrency());
    return new ResponseEntity<>((TransferResponseDto) commandGateway.sendAndWait(command), HttpStatus.ACCEPTED);
  }

  @GetMapping("account/{accountId}")
  private ResponseEntity<WalletDto> findWalletByAccountIdAndCurrency(@PathVariable UUID accountId, @RequestParam Currency currency) {
    return ResponseEntity.ok(queryGateway.query(new FindWalletByAccountIdAndCurrencyQuery(accountId, currency), WalletDto.class).join());
  }
}
