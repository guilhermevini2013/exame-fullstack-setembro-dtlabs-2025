package com.example.devicemonitoringapi.domain.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

import java.util.UUID;

@Entity
public class Device {
    @Id
    private final UUID id = UUID.randomUUID();
    @ManyToOne(optional = false )
    private User user;

    public Device() {

    }
}
