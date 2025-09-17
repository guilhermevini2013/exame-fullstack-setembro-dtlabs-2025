package com.example.devicemonitoringapi.infrastructure.repositories;

import com.example.devicemonitoringapi.domain.models.Device;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface DeviceRepository extends JpaRepository<Device, UUID> {
    Optional<Device> findByIdAndUserName(UUID id, String userName);
    boolean existsByIdAndUserName(UUID id, String userName);
    void deleteByIdAndUserName(UUID id, String userName);
    Page<Device> findAllByUserName(String userName, Pageable pageable);
}
