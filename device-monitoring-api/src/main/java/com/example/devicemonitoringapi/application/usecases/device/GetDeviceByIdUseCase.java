package com.example.devicemonitoringapi.application.usecases.device;

import com.example.devicemonitoringapi.domain.exceptions.DeviceNotFound;
import com.example.devicemonitoringapi.domain.models.Device;
import com.example.devicemonitoringapi.domain.models.User;
import com.example.devicemonitoringapi.dtos.device.DeviceSavedDTO;
import com.example.devicemonitoringapi.infrastructure.repositories.DeviceRepository;
import com.example.devicemonitoringapi.infrastructure.utils.SecurityUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class GetDeviceByIdUseCase {
    public final DeviceRepository deviceRepository;

    public GetDeviceByIdUseCase(DeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
    }

    @Transactional
    public DeviceSavedDTO execute(String id) {
        User currentUser = SecurityUtils.getCurrentUser();
        assert currentUser != null;
        Device device = deviceRepository.findByIdAndUserName(UUID.fromString(id), currentUser.getUsername()).orElseThrow(() -> new DeviceNotFound("Device not found for the user."));
        return new DeviceSavedDTO(device);
    }
}
