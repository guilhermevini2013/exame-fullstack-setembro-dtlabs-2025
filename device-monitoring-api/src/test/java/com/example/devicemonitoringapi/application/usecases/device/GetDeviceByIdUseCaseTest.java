package com.example.devicemonitoringapi.application.usecases.device;

import com.example.devicemonitoringapi.domain.exceptions.DeviceNotFound;
import com.example.devicemonitoringapi.domain.models.Device;
import com.example.devicemonitoringapi.domain.models.User;
import com.example.devicemonitoringapi.dtos.device.DeviceSavedDTO;
import com.example.devicemonitoringapi.infrastructure.repositories.DeviceRepository;
import com.example.devicemonitoringapi.infrastructure.utils.SecurityUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetDeviceByIdUseCaseTest {

    @Mock
    private DeviceRepository deviceRepository;

    @InjectMocks
    private GetDeviceByIdUseCase useCase;

    @Test
    @DisplayName("should return device when owned by current user")
    void get_success() {
        User user = org.mockito.Mockito.mock(User.class);
        when(user.getId()).thenReturn(java.util.UUID.randomUUID());
        when(user.getUsername()).thenReturn("alice");
        UUID deviceId = UUID.randomUUID();
        Device device = new Device(user, "dev1", "lab", "desc", "SN-1234567890");
        try (var mocked = org.mockito.Mockito.mockStatic(SecurityUtils.class)) {
            mocked.when(SecurityUtils::getCurrentUser).thenReturn(user);
            when(deviceRepository.findByIdAndUserName(eq(deviceId), eq("alice"))).thenReturn(Optional.of(device));

            DeviceSavedDTO dto = useCase.execute(deviceId.toString());

            assertNotNull(dto);
            assertEquals("dev1", dto.name());
            assertEquals("lab", dto.location());
            assertEquals("desc", dto.description());
            assertEquals("SN-1234567890", dto.sn());
        }
    }

    @Test
    @DisplayName("should throw DeviceNotFound when not owned or missing")
    void get_notFound() {
        User user = new User("bob", "pwd");
        UUID deviceId = UUID.randomUUID();
        try (var mocked = org.mockito.Mockito.mockStatic(SecurityUtils.class)) {
            mocked.when(SecurityUtils::getCurrentUser).thenReturn(user);
            when(deviceRepository.findByIdAndUserName(any(UUID.class), eq("bob"))).thenReturn(Optional.empty());

            assertThrows(DeviceNotFound.class, () -> useCase.execute(deviceId.toString()));
        }
    }
}
