package com.nerdysoft.walletservice.controller;

import com.nerdysoft.model.exception.ExceptionHandlerResponse;
import com.nerdysoft.model.exception.UniqueException;
import feign.FeignException;
import java.time.LocalDateTime;
import org.axonframework.commandhandling.CommandExecutionException;
import org.axonframework.queryhandling.QueryExecutionException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionHandlerController {
  @ExceptionHandler(value = {QueryExecutionException.class})
  private ResponseEntity<ExceptionHandlerResponse> handleQueryExecutionException(QueryExecutionException e) {
    return new ResponseEntity<>(new ExceptionHandlerResponse(e.getMessage(), HttpStatus.NOT_FOUND.value(), LocalDateTime.now()),
        HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(value = {CommandExecutionException.class})
  private ResponseEntity<ExceptionHandlerResponse> handleCommandExecutionException(CommandExecutionException e) {
    return new ResponseEntity<>(new ExceptionHandlerResponse(e.getMessage(), HttpStatus.BAD_REQUEST.value(), LocalDateTime.now()),
        HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(value = {FeignException.class})
  private ResponseEntity<ExceptionHandlerResponse> handleFeignException(FeignException e) {
    return new ResponseEntity<>(new ExceptionHandlerResponse(e.getMessage(), HttpStatus.BAD_REQUEST.value(), LocalDateTime.now()),
        HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler(value = {IllegalArgumentException.class})
  private ResponseEntity<ExceptionHandlerResponse> handleIllegalArgumentException(IllegalArgumentException e) {
    return new ResponseEntity<>(new ExceptionHandlerResponse(e.getMessage(), HttpStatus.BAD_REQUEST.value(), LocalDateTime.now()),
        HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler(value = {UniqueException.class})
  private ResponseEntity<ExceptionHandlerResponse> handleUniqueException(UniqueException e) {
    return new ResponseEntity<>(new ExceptionHandlerResponse(e.getMessage(), e.getHttpStatus().value(), LocalDateTime.now()),
        e.getHttpStatus());
  }
}
