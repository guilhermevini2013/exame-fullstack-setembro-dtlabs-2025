package com.example.devicemonitoringapi.domain.models;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "users")
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String name;
    @Column(name = "user_password")
    private String password;
    private Instant createdAt;
    @OneToMany(mappedBy = "user")
    private List<Device> devices;

    protected User() {}

    public User(String name, String password) {
        this.name = name;
        this.password = password;
        this.createdAt = Instant.now();
        this.devices = List.of();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of((GrantedAuthority) () -> "USER");
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.name;
    }

    private static class Builder {
        private String name;
        private String password;

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setPassword(String password) {
            this.password = password;
            return this;
        }

        public User build(){
            return new User(this.name, this.password);
        }
    }
}
