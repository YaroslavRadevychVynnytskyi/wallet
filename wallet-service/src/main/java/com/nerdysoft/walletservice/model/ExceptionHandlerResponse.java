package com.nerdysoft.walletservice.model;

import java.time.LocalDateTime;

public record ExceptionHandlerResponse(String message, Integer statusCode, LocalDateTime timestamp) {}
