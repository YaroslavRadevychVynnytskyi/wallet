package com.nerdysoft.walletservice.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public class AccountHasAlreadyWalletOnThisCurrency extends RuntimeException {
  private final String message = "Account has already wallet on this currency";
  private final Integer httpStatus = HttpStatus.NOT_ACCEPTABLE.value();
}
