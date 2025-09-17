package com.example.devicemonitoringapi.controllers;

import com.example.devicemonitoringapi.application.usecases.device.CreateDeviceUseCase;
import com.example.devicemonitoringapi.application.usecases.device.DeleteDeviceByIdUseCase;
import com.example.devicemonitoringapi.application.usecases.device.GetAllDevicesUseCase;
import com.example.devicemonitoringapi.application.usecases.device.GetDeviceByIdUseCase;
import com.example.devicemonitoringapi.domain.exceptions.DeviceNotFound;
import com.example.devicemonitoringapi.dtos.ErrorResponseDTO;
import com.example.devicemonitoringapi.dtos.SuccessResponseDTO;
import com.example.devicemonitoringapi.dtos.device.DeviceCreateDTO;
import com.example.devicemonitoringapi.dtos.device.DeviceSavedDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.UUID;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/devices")
public class DeviceController {
    private final CreateDeviceUseCase createDeviceUseCase;
    private final GetDeviceByIdUseCase getDeviceByIdUseCase;
    private final DeleteDeviceByIdUseCase deleteDeviceByIdUseCase;
    private final GetAllDevicesUseCase getAllDevicesUseCase;

    public DeviceController(CreateDeviceUseCase createDeviceUseCase, GetDeviceByIdUseCase getDeviceByIdUseCase, DeleteDeviceByIdUseCase deleteDeviceByIdUseCase, GetAllDevicesUseCase getAllDevicesUseCase) {
        this.createDeviceUseCase = createDeviceUseCase;
        this.getDeviceByIdUseCase = getDeviceByIdUseCase;
        this.deleteDeviceByIdUseCase = deleteDeviceByIdUseCase;
        this.getAllDevicesUseCase = getAllDevicesUseCase;
    }

    @PostMapping(value = "/create")
    public ResponseEntity<SuccessResponseDTO> createDevice(@RequestBody @Valid DeviceCreateDTO deviceCreateDTO) {
        DeviceSavedDTO deviceSavedDTO = createDeviceUseCase.execute(deviceCreateDTO);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .replacePath("/devices/{id}")
                .buildAndExpand(deviceSavedDTO.id())
                .toUri();

        return ResponseEntity.created(location).body(new SuccessResponseDTO(201, "Device created with success.", deviceSavedDTO));
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<SuccessResponseDTO> getDevice(@PathVariable @Valid @UUID(message = "Id is not a valid UUID.") @NotBlank(message = "Id is blank") String id) {
        DeviceSavedDTO deviceSavedDTO = getDeviceByIdUseCase.execute(id);
        return ResponseEntity.ok(new SuccessResponseDTO(200, "Device found with success.", deviceSavedDTO));
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<SuccessResponseDTO> deleteDevice(@PathVariable @Valid @UUID(message = "Id is not a valid UUID.") @NotBlank(message = "Id is blank") String id) {
        deleteDeviceByIdUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping()
    public ResponseEntity<SuccessResponseDTO> findAllLikeByCity(@RequestParam(name = "linesPerPage", defaultValue = "20") Integer linesPerPage,
                                                                @RequestParam(name = "page", defaultValue = "0") Integer page,
                                                                @RequestParam(name = "direction", defaultValue = "ASC") String direction,
                                                                @RequestParam(name = "orderBy", defaultValue = "id") String orderBy) {
        List<DeviceSavedDTO> allDevices = getAllDevicesUseCase.execute(PageRequest.of(page, linesPerPage, Sort.Direction.fromString(direction), orderBy));
        return ResponseEntity.ok(new SuccessResponseDTO(200, "Devices found with success.", allDevices));
    }

    @ExceptionHandler(DeviceNotFound.class)
    public ResponseEntity<ErrorResponseDTO> handleUserNotFound(DeviceNotFound ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponseDTO(HttpStatus.NOT_FOUND.value(), ex.getMessage(), request.getServletPath()));
    }

}
