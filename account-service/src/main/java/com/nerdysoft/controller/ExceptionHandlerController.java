package com.nerdysoft.controller;

import com.nerdysoft.model.exception.ExceptionHandlerResponse;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionHandlerController {
  @ExceptionHandler(value = {EntityNotFoundException.class})
  private ResponseEntity<ExceptionHandlerResponse> handleEntityNotFoundException(EntityNotFoundException e) {
    return new ResponseEntity<>(new ExceptionHandlerResponse(e.getMessage(), HttpStatus.NOT_FOUND.value(), LocalDateTime.now()),
        HttpStatus.NOT_FOUND);
  }
}
