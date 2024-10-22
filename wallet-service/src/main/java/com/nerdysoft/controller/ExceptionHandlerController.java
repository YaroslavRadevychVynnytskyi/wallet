package com.nerdysoft.controller;

import com.nerdysoft.exception.UniqueException;
import com.nerdysoft.entity.ExceptionHandlerResponse;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionHandlerController {
  @ExceptionHandler(value = {EntityNotFoundException.class})
  private ResponseEntity<ExceptionHandlerResponse> handleEntityNotFoundException() {
    return new ResponseEntity<>(new ExceptionHandlerResponse("Entity with this id is absent", HttpStatus.NOT_FOUND.value(), LocalDateTime.now()),
        HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(value = {UniqueException.class})
  private ResponseEntity<ExceptionHandlerResponse> handleUniqueException(UniqueException e) {
    return new ResponseEntity<>(new ExceptionHandlerResponse(e.getMessage(), e.getHttpStatus().value(), LocalDateTime.now()),
        e.getHttpStatus());
  }
}
