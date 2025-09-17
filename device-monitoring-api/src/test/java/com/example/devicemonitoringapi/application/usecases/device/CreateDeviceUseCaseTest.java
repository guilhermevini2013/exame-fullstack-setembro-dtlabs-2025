package com.example.devicemonitoringapi.application.usecases.device;

import com.example.devicemonitoringapi.domain.exceptions.IntegrityConstraintViolationException;
import com.example.devicemonitoringapi.domain.models.Device;
import com.example.devicemonitoringapi.domain.models.User;
import com.example.devicemonitoringapi.dtos.device.DeviceCreateDTO;
import com.example.devicemonitoringapi.dtos.device.DeviceSavedDTO;
import com.example.devicemonitoringapi.infrastructure.repositories.DeviceRepository;
import com.example.devicemonitoringapi.infrastructure.utils.SecurityUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateDeviceUseCaseTest {

    @Mock
    private DeviceRepository deviceRepository;

    @InjectMocks
    private CreateDeviceUseCase useCase;

    @Test
    @DisplayName("should create device for current user and return DTO")
    void create_success() {
        User user = org.mockito.Mockito.mock(User.class);
        java.util.UUID uid = java.util.UUID.randomUUID();
        when(user.getId()).thenReturn(uid);
        DeviceCreateDTO input = new DeviceCreateDTO("dev1", "lab", "desc", "SN-1234567890");
        Device persisted = new Device(user, input.name(), input.location(), input.description(), input.sn());
        try (var mocked = org.mockito.Mockito.mockStatic(SecurityUtils.class)) {
            mocked.when(SecurityUtils::getCurrentUser).thenReturn(user);
            when(deviceRepository.save(any(Device.class))).thenReturn(persisted);

            DeviceSavedDTO dto = useCase.execute(input);

            assertNotNull(dto);
            assertEquals("dev1", dto.name());
            verify(deviceRepository).save(any(Device.class));
            verify(deviceRepository).flush();
        }
    }

    @Test
    @DisplayName("should map DataIntegrityViolationException to IntegrityConstraintViolationException")
    void create_duplicate_sn_throwsIntegrity() {
        User user = new User("alice", "pwd");
        DeviceCreateDTO input = new DeviceCreateDTO("dev1", "lab", "desc", "SN-1234567890");
        try (var mocked = org.mockito.Mockito.mockStatic(SecurityUtils.class)) {
            mocked.when(SecurityUtils::getCurrentUser).thenReturn(user);
            when(deviceRepository.save(any(Device.class))).thenThrow(new DataIntegrityViolationException("dup"));

            assertThrows(IntegrityConstraintViolationException.class, () -> useCase.execute(input));
            verify(deviceRepository, never()).flush();
        }
    }
}
