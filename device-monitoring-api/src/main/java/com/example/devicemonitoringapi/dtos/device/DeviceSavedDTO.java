package com.example.devicemonitoringapi.dtos.device;

import com.example.devicemonitoringapi.domain.models.Device;

public record DeviceSavedDTO(
        String id,
        String name,
        String location,
        String description,
        String sn,
        String userId
) {
    public DeviceSavedDTO(Device device) {
        this(device.getId().toString(), device.getName(), device.getLocation(), device.getDescription(), device.getSn(), device.getUser().getId().toString());
    }
}
