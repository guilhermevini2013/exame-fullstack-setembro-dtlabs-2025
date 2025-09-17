package com.example.devicemonitoringapi.infrastructure.utils;

import com.example.devicemonitoringapi.domain.models.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class SecurityUtils {

    private SecurityUtils() {}

    public static User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof UserDetails userDetails) {
            return (User) userDetails;
        } else {
            throw new UsernameNotFoundException("User not authenticated.");
        }
    }
}
