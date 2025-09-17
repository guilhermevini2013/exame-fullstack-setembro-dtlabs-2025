package com.example.devicemonitoringapi.domain.models;

import jakarta.persistence.*;
import org.hibernate.annotations.Check;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Check(constraints = "char_length(sn) = 12")
public class Device {
    @Id
    private final UUID id = UUID.randomUUID();
    @ManyToOne(optional = false )
    private User user;
    private String name;
    private String location;
    private String description;
    @Column(length = 12, nullable = false, unique = true)
    private String sn;
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    @Column
    private LocalDateTime updatedAt;

    protected Device() {

    }

    public Device(User user, String name, String location, String description, String sn) {
        this.user = user;
        this.name = name;
        this.location = location;
        this.description = description;
        this.sn = sn;
    }

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }

    public String getDescription() {
        return description;
    }

    public String getSn() {
        return sn;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public static class Builder {
        private User user;
        private String name;
        private String location;
        private String description;
        private String sn;

        public Builder setUser(User user) {
            this.user = user;
            return this;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setLocation(String location) {
            this.location = location;
            return this;
        }

        public Builder setDescription(String description) {
            this.description = description;
            return this;
        }

        public Builder setSn(String sn) {
            this.sn = sn;
            return this;
        }

        public Device build() {
            return new Device(this.user, this.name, this.location, this.description, this.sn);
        }
    }
}
