package com.example.devicemonitoringapi.controllers;

import com.example.devicemonitoringapi.application.usecases.user.AuthUseCase;
import com.example.devicemonitoringapi.dtos.ErrorResponseDTO;
import com.example.devicemonitoringapi.dtos.SuccessResponseDTO;
import com.example.devicemonitoringapi.dtos.user.UserAuthDTO;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private AuthUseCase authUseCase;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private UserController controller;

    @Test
    @DisplayName("auth should call use case, set cookie, and return 200 with SuccessResponseDTO")
    void auth_success_setsCookie_and_returnsOk() {
        UserAuthDTO body = new UserAuthDTO("maria", "1234");
        when(authUseCase.execute(body)).thenReturn("fake-token");

        ResponseEntity<SuccessResponseDTO> entity = controller.auth(body, response);

        assertEquals(200, entity.getStatusCode().value());
        assertEquals(HttpStatus.OK, entity.getStatusCode());
        assertNotNull(entity.getBody());
        assertEquals(HttpStatus.OK.value(), entity.getBody().code());
        assertEquals("Success Auth", entity.getBody().message());

        ArgumentCaptor<Cookie> cookieCaptor = ArgumentCaptor.forClass(Cookie.class);
        verify(response).addCookie(cookieCaptor.capture());
        Cookie cookie = cookieCaptor.getValue();
        assertEquals("token", cookie.getName());
        assertEquals("fake-token", cookie.getValue());
        assertEquals("/", cookie.getPath());
        assertEquals(500000, cookie.getMaxAge());
        assertFalse(cookie.isHttpOnly());

        verify(authUseCase).execute(body);
    }

    @Test
    @DisplayName("handleUserNotFound should map BadCredentialsException to 401 with expected ErrorResponseDTO")
    void handle_badCredentials_returnsUnauthorized() {
        when(request.getServletPath()).thenReturn("/api/user/auth");

        ResponseEntity<ErrorResponseDTO> entity = controller.handleUserNotFound(request);

        assertEquals(HttpStatus.UNAUTHORIZED, entity.getStatusCode());
        assertNotNull(entity.getBody());
        assertEquals(HttpStatus.UNAUTHORIZED.value(), entity.getBody().code());
        assertEquals("Nonexistent user or invalid password", entity.getBody().message());
        assertEquals("/api/user/auth", entity.getBody().path());
    }
}
