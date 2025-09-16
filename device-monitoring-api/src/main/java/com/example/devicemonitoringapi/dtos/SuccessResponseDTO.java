package com.example.devicemonitoringapi.dtos;

public record SuccessResponseDTO(
        Integer code,
        String message,
        Object data
) {
}
