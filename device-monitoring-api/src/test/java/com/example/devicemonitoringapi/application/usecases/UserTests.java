package com.example.devicemonitoringapi.application.usecases;

import com.example.devicemonitoringapi.application.services.JwtService;
import com.example.devicemonitoringapi.application.usecases.user.AuthUseCase;
import com.example.devicemonitoringapi.domain.models.User;
import com.example.devicemonitoringapi.dtos.user.UserAuthDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
public class UserTests {
    @Mock
    private AuthenticationManager authenticationManager;
    @InjectMocks
    private AuthUseCase authUseCase;
    @Mock
    private JwtService jwtService;

    @BeforeEach
    void setUp() {

    }

    @Test
    @DisplayName("Should authenticate the user and return a JWT token")
    void testUserAuth() {
        UserAuthDTO userValid = new UserAuthDTO("maria", "1234");

        User user = new User("maria", "1234");

        Authentication authentication =
                new UsernamePasswordAuthenticationToken(user, null);

        when(authenticationManager.authenticate(any(Authentication.class)))
                .thenReturn(authentication);

        when(jwtService.generateToken("maria"))
                .thenReturn("fake-jwt-token");

        String token = authUseCase.execute(userValid);

        assertEquals("fake-jwt-token", token);

        verify(authenticationManager).authenticate(any(Authentication.class));
        verify(jwtService).generateToken("maria");
    }

}
