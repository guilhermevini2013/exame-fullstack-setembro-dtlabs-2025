package com.example.devicemonitoringapi.application.usecases.device;

import com.example.devicemonitoringapi.domain.models.User;
import com.example.devicemonitoringapi.dtos.device.DeviceSavedDTO;
import com.example.devicemonitoringapi.infrastructure.repositories.DeviceRepository;
import com.example.devicemonitoringapi.infrastructure.utils.SecurityUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Component
public class GetAllDevicesUseCase {

    public final DeviceRepository deviceRepository;

    public GetAllDevicesUseCase(DeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
    }

    @Transactional(readOnly = true)
    public List<DeviceSavedDTO> execute(PageRequest pageRequest) {
        User currentUser = SecurityUtils.getCurrentUser();
        assert currentUser != null;
        return deviceRepository.findAllByUserName(currentUser.getUsername(), pageRequest).map(DeviceSavedDTO::new).toList();
    }
}
