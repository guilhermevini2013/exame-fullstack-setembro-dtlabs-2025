package com.example.devicemonitoringapi.dtos.device;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record DeviceCreateDTO(
        @NotBlank(message = "Name cannot be blank.")
        String name,
        @NotBlank(message = "Location cannot be blank.")
        String location,
        @Size(min = 12, max = 12, message = "SN must be 12 characters.")
        @NotBlank(message = "Sn cannot be blank.")
        String sn,
        String description) {
}
