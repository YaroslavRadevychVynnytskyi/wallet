package com.nerdysoft.entity;

import java.time.LocalDateTime;

public record ExceptionHandlerResponse(String message, Integer statusCode, LocalDateTime timestamp) {}
