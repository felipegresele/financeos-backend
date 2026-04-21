package com.financeos.exception;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
class ErrorResponse {
    private final int status;
    private final String error;
    private final String message;
    private final LocalDateTime timestamp = LocalDateTime.now();
    private Map<String, String> errors;

    public ErrorResponse(int status, String error, String message) {
        this.status = status;
        this.error = error;
        this.message = message;
    }

    public ErrorResponse withErrors(Map<String, String> errors) {
        this.errors = errors;
        return this;
    }
}
