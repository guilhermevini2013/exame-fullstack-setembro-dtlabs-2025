package com.example.devicemonitoringapi.domain.exceptions;

public class DeviceNotFound extends RuntimeException{
    public DeviceNotFound(String message) {
        super(message);
    }
}
