package com.example.devicemonitoringapi.controllers;

import com.example.devicemonitoringapi.domain.exceptions.IntegrityConstraintViolationException;
import com.example.devicemonitoringapi.dtos.ErrorResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleUserNotFound(UsernameNotFoundException ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponseDTO(HttpStatus.UNAUTHORIZED.value(), ex.getMessage(), request.getServletPath()));
    }

    @ExceptionHandler(IntegrityConstraintViolationException.class)
    public ResponseEntity<ErrorResponseDTO> handleUserNotFound(IntegrityConstraintViolationException ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponseDTO(HttpStatus.BAD_REQUEST.value(), ex.getMessage(), request.getServletPath()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> validation(MethodArgumentNotValidException e, HttpServletRequest request) {
        return ResponseEntity.badRequest().body(new ErrorResponseDTO(HttpStatus.BAD_REQUEST.value(), e.getFieldErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).toList(), request.getRequestURI()));
    }
}
