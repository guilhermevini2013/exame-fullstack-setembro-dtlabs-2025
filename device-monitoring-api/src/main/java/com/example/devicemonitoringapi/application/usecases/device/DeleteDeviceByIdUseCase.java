package com.example.devicemonitoringapi.application.usecases.device;

import com.example.devicemonitoringapi.domain.exceptions.DeviceNotFound;
import com.example.devicemonitoringapi.domain.models.User;
import com.example.devicemonitoringapi.infrastructure.repositories.DeviceRepository;
import com.example.devicemonitoringapi.infrastructure.utils.SecurityUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class DeleteDeviceByIdUseCase {
    public final DeviceRepository deviceRepository;

    public DeleteDeviceByIdUseCase(DeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
    }

    @Transactional
    public void execute(String id) {
        User currentUser = SecurityUtils.getCurrentUser();
        assert currentUser != null;
        boolean existsByIdAndUserName = deviceRepository.existsByIdAndUserName(UUID.fromString(id), currentUser.getUsername());
        if (!existsByIdAndUserName) {
            throw new DeviceNotFound("Device not found for the user.");
        }
        deviceRepository.deleteByIdAndUserName(UUID.fromString(id), currentUser.getUsername());
    }
}
