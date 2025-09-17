package com.example.devicemonitoringapi.application.usecases.device;

import com.example.devicemonitoringapi.domain.exceptions.IntegrityConstraintViolationException;
import com.example.devicemonitoringapi.domain.models.Device;
import com.example.devicemonitoringapi.domain.models.User;
import com.example.devicemonitoringapi.dtos.device.DeviceCreateDTO;
import com.example.devicemonitoringapi.dtos.device.DeviceSavedDTO;
import com.example.devicemonitoringapi.infrastructure.repositories.DeviceRepository;
import com.example.devicemonitoringapi.infrastructure.utils.SecurityUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CreateDeviceUseCase {
    public final DeviceRepository deviceRepository;

    public CreateDeviceUseCase(DeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
    }

    @Transactional
    public DeviceSavedDTO execute(DeviceCreateDTO deviceCreateDTO) {
        User currentUser = SecurityUtils.getCurrentUser();
        Device device = new Device.Builder().setUser(currentUser)
                .setName(deviceCreateDTO.name())
                .setLocation(deviceCreateDTO.location())
                .setDescription(deviceCreateDTO.description())
                .setSn(deviceCreateDTO.sn()).build();
        try {
            Device deviceSaved = deviceRepository.save(device);
            deviceRepository.flush();
            return new DeviceSavedDTO(deviceSaved);
        } catch (DataIntegrityViolationException e) {
            throw new IntegrityConstraintViolationException("Device already exists with the same SN.");
        }
    }
}
