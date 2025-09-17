package com.example.devicemonitoringapi.application.services;

import com.example.devicemonitoringapi.domain.models.User;
import com.example.devicemonitoringapi.infrastructure.repositories.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AuthService authService;

    @Test
    @DisplayName("loadUserByUsername should return UserDetails when user exists")
    void loadUserByUsername_success() {
        User maria = new User("maria", "1234");
        when(userRepository.findByName("maria")).thenReturn(Optional.of(maria));

        UserDetails details = authService.loadUserByUsername("maria");

        assertNotNull(details);
        assertEquals("maria", details.getUsername());
        verify(userRepository).findByName("maria");
    }

    @Test
    @DisplayName("loadUserByUsername should throw UsernameNotFoundException when user does not exist")
    void loadUserByUsername_notFound() {
        when(userRepository.findByName("john")).thenReturn(Optional.empty());

        UsernameNotFoundException ex = assertThrows(
                UsernameNotFoundException.class,
                () -> authService.loadUserByUsername("john")
        );

        assertEquals("User not found.", ex.getMessage());
        verify(userRepository).findByName("john");
    }
}
