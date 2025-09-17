package com.example.devicemonitoringapi.controllers;

import com.example.devicemonitoringapi.application.usecases.device.CreateDeviceUseCase;
import com.example.devicemonitoringapi.application.usecases.device.DeleteDeviceByIdUseCase;
import com.example.devicemonitoringapi.application.usecases.device.GetDeviceByIdUseCase;
import com.example.devicemonitoringapi.domain.exceptions.DeviceNotFound;
import com.example.devicemonitoringapi.dtos.ErrorResponseDTO;
import com.example.devicemonitoringapi.dtos.SuccessResponseDTO;
import com.example.devicemonitoringapi.dtos.device.DeviceCreateDTO;
import com.example.devicemonitoringapi.dtos.device.DeviceSavedDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeviceControllerTest {

    @Mock
    private CreateDeviceUseCase createDeviceUseCase;

    @Mock
    private GetDeviceByIdUseCase getDeviceByIdUseCase;

    @Mock
    private DeleteDeviceByIdUseCase deleteDeviceByIdUseCase;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private DeviceController controller;

    @Test
    @DisplayName("createDevice should return 201 with location header and SuccessResponseDTO")
    void createDevice_success() {
        String id = UUID.randomUUID().toString();
        DeviceSavedDTO saved = new DeviceSavedDTO(id, "dev1", "lab", "desc", "SN1234567890", null);
        when(createDeviceUseCase.execute(any(DeviceCreateDTO.class))).thenReturn(saved);

        DeviceCreateDTO body = new DeviceCreateDTO("dev1", "lab", "desc", "SN1234567890");
        try (var mocked = org.mockito.Mockito.mockStatic(ServletUriComponentsBuilder.class)) {
            var builder = org.mockito.Mockito.mock(ServletUriComponentsBuilder.class);
            var uriComponents = org.springframework.web.util.UriComponentsBuilder
                    .fromUriString("http://localhost")
                    .path("/devices/{id}")
                    .buildAndExpand(id);
            when(builder.replacePath("/devices/{id}")).thenReturn(builder);
            when(builder.buildAndExpand(id)).thenReturn(uriComponents);
            mocked.when(ServletUriComponentsBuilder::fromCurrentRequest).thenReturn(builder);
            ResponseEntity<SuccessResponseDTO> entity = controller.createDevice(body);

            assertEquals(HttpStatus.CREATED, entity.getStatusCode());
            assertNotNull(entity.getHeaders().getLocation());
            URI loc = entity.getHeaders().getLocation();
            assertTrue(loc.getPath().endsWith("/devices/" + id));
            assertNotNull(entity.getBody());
            assertEquals(201, entity.getBody().code());
            assertEquals("Device created with success.", entity.getBody().message());
            assertEquals(saved, entity.getBody().data());

            verify(createDeviceUseCase).execute(body);
        }
    }

    @Test
    @DisplayName("getDevice should return 200 and SuccessResponseDTO when device belongs to current user")
    void getDevice_success() {
        String id = UUID.randomUUID().toString();
        DeviceSavedDTO dto = new DeviceSavedDTO(id, "dev1", "lab", "desc", "SN1234567890", null);
        when(getDeviceByIdUseCase.execute(id)).thenReturn(dto);

        ResponseEntity<SuccessResponseDTO> entity = controller.getDevice(id);

        assertEquals(HttpStatus.OK, entity.getStatusCode());
        assertNotNull(entity.getBody());
        assertEquals(200, entity.getBody().code());
        assertEquals("Device found with success.", entity.getBody().message());
        assertEquals(dto, entity.getBody().data());
    }

    @Test
    @DisplayName("getDevice should map DeviceNotFound to 404 using controller @ExceptionHandler")
    void getDevice_notFound_mappedByHandler() {
        when(request.getServletPath()).thenReturn("/api/devices/" + UUID.randomUUID());
        DeviceNotFound ex = new DeviceNotFound("Device not found for the user.");

        ResponseEntity<ErrorResponseDTO> entity = controller.handleUserNotFound(ex, request);

        assertEquals(HttpStatus.NOT_FOUND, entity.getStatusCode());
        assertNotNull(entity.getBody());
        assertEquals(HttpStatus.NOT_FOUND.value(), entity.getBody().code());
        assertEquals("Device not found for the user.", entity.getBody().message());
        assertEquals(request.getServletPath(), entity.getBody().path());
    }

    @Test
    @DisplayName("deleteDevice should return 204 when device belongs to current user")
    void deleteDevice_success() {
        String id = UUID.randomUUID().toString();

        ResponseEntity<SuccessResponseDTO> entity = controller.deleteDevice(id);

        assertEquals(HttpStatus.NO_CONTENT, entity.getStatusCode());
        assertNull(entity.getBody());
        verify(deleteDeviceByIdUseCase).execute(eq(id));
    }

    @Test
    @DisplayName("deleteDevice should return 404 via handler when other user tries to delete a device that is not his")
    void deleteDevice_otherUser_attemptsDeletion_shouldReturn404() {
        when(request.getServletPath()).thenReturn("/api/devices/" + UUID.randomUUID());
        DeviceNotFound ex = new DeviceNotFound("Device not found for the user.");

        ResponseEntity<ErrorResponseDTO> entity = controller.handleUserNotFound(ex, request);

        assertEquals(HttpStatus.NOT_FOUND, entity.getStatusCode());
        assertNotNull(entity.getBody());
        assertEquals(HttpStatus.NOT_FOUND.value(), entity.getBody().code());
        assertEquals("Device not found for the user.", entity.getBody().message());
        assertEquals(request.getServletPath(), entity.getBody().path());
    }
}
