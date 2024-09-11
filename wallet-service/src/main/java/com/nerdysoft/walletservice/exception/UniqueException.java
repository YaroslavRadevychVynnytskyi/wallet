package com.nerdysoft.walletservice.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public class UniqueException extends RuntimeException {
  private String message;
  private HttpStatus httpStatus;
}
