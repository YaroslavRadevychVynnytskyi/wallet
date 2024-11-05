package com.nerdysoft.model.exception;

import java.time.LocalDateTime;

public record ExceptionHandlerResponse(String message, Integer statusCode, LocalDateTime timestamp) {}
