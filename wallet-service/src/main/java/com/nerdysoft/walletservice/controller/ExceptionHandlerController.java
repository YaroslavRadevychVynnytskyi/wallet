package com.nerdysoft.walletservice.controller;

import com.nerdysoft.walletservice.exception.AccountHasAlreadyWalletOnThisCurrencyException;
import com.nerdysoft.walletservice.model.RequestError;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionHandlerController {
  @ExceptionHandler(value = {EntityNotFoundException.class})
  private ResponseEntity<RequestError> handleEntityNotFoundException() {
    return new ResponseEntity<>(new RequestError("Entity with this id is absent", HttpStatus.NOT_FOUND.value()),
        HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(value = {AccountHasAlreadyWalletOnThisCurrencyException.class})
  private ResponseEntity<RequestError> handleAccountHasAlreadyWalletOnThisCurrencyException(AccountHasAlreadyWalletOnThisCurrencyException e) {
    return new ResponseEntity<>(new RequestError(e.getMessage(), e.getHttpStatus()),
        HttpStatus.NOT_ACCEPTABLE);
  }
}
