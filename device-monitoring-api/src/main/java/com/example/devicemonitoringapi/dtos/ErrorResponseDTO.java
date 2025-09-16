package com.example.devicemonitoringapi.dtos;

public record ErrorResponseDTO(
        Integer code,
        String message,
        String path
) {
}
