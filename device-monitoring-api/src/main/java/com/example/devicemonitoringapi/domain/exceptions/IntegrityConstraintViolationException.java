package com.example.devicemonitoringapi.domain.exceptions;

public class IntegrityConstraintViolationException extends RuntimeException {
    public IntegrityConstraintViolationException(String message) {
        super(message);
    }
}
