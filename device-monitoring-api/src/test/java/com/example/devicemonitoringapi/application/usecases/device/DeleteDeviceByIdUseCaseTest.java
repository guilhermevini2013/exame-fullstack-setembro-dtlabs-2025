package com.example.devicemonitoringapi.application.usecases.device;

import com.example.devicemonitoringapi.domain.exceptions.DeviceNotFound;
import com.example.devicemonitoringapi.domain.models.User;
import com.example.devicemonitoringapi.infrastructure.repositories.DeviceRepository;
import com.example.devicemonitoringapi.infrastructure.utils.SecurityUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteDeviceByIdUseCaseTest {

    @Mock
    private DeviceRepository deviceRepository;

    @InjectMocks
    private DeleteDeviceByIdUseCase useCase;

    @Test
    @DisplayName("should delete when device exists for current user")
    void delete_success_whenOwnedByCurrentUser() {
        // Arrange current user via SecurityUtils static method using a spy or wrapper
        User user = new User("alice", "pwd");
        try (var mocked = org.mockito.Mockito.mockStatic(SecurityUtils.class)) {
            mocked.when(SecurityUtils::getCurrentUser).thenReturn(user);
            when(deviceRepository.existsByIdAndUserName(any(UUID.class), eq("alice"))).thenReturn(true);

            // Act
            useCase.execute(UUID.randomUUID().toString());

            // Assert
            verify(deviceRepository).deleteByIdAndUserName(any(UUID.class), eq("alice"));
        }
    }

    @Test
    @DisplayName("should throw DeviceNotFound when other user tries to delete or device does not exist")
    void delete_throws_whenNotOwnedOrMissing() {
        User user = new User("bob", "pwd");
        try (var mocked = org.mockito.Mockito.mockStatic(SecurityUtils.class)) {
            mocked.when(SecurityUtils::getCurrentUser).thenReturn(user);
            when(deviceRepository.existsByIdAndUserName(any(UUID.class), eq("bob"))).thenReturn(false);

            DeviceNotFound ex = assertThrows(DeviceNotFound.class, () -> useCase.execute(UUID.randomUUID().toString()));
            assertEquals("Device not found for the user.", ex.getMessage());
            verify(deviceRepository, never()).deleteByIdAndUserName(any(UUID.class), any());
        }
    }
}
