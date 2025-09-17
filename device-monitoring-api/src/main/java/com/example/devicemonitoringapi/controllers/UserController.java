package com.example.devicemonitoringapi.controllers;

import com.example.devicemonitoringapi.application.usecases.user.AuthUseCase;
import com.example.devicemonitoringapi.dtos.ErrorResponseDTO;
import com.example.devicemonitoringapi.dtos.SuccessResponseDTO;
import com.example.devicemonitoringapi.dtos.user.UserAuthDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.Cookie;

@RestController
@RequestMapping("/api/user")
public class UserController {
    private final AuthUseCase authUseCase;

    public UserController(AuthUseCase authUseCase) {
        this.authUseCase = authUseCase;
    }

    @PostMapping("/auth")
    public ResponseEntity<SuccessResponseDTO> auth(@RequestBody UserAuthDTO userAuthDTO, HttpServletResponse response) {
        String token = authUseCase.execute(userAuthDTO);
        Cookie cookie = new Cookie("token", token);
        cookie.setPath("/");
        cookie.setMaxAge(500000);
        cookie.setHttpOnly(false);
        response.addCookie(cookie);
        return ResponseEntity.ok(new SuccessResponseDTO(HttpStatus.OK.value(), "Success Auth", ""));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponseDTO> handleUserNotFound(HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponseDTO(HttpStatus.UNAUTHORIZED.value(), "Nonexistent user or invalid password", request.getServletPath()));
    }
}
