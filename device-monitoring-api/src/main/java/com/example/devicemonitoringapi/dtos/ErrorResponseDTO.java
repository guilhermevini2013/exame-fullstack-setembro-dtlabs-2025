package com.example.devicemonitoringapi.dtos;

public record ErrorResponseDTO(
        Integer code,
        Object message,
        String path
) {
}
