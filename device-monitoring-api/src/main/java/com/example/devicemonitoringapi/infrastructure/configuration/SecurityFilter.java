package com.example.devicemonitoringapi.infrastructure.configuration;

import com.example.devicemonitoringapi.application.services.JwtService;
import com.example.devicemonitoringapi.domain.models.User;
import com.example.devicemonitoringapi.infrastructure.repositories.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.WebUtils;

import java.io.IOException;

@Component
public class SecurityFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserRepository userRepository;

    public SecurityFilter(JwtService jwtService, UserRepository userRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        HttpServletRequest httpRequest = request;
        Cookie cookie = WebUtils.getCookie(httpRequest, "token");
        if (cookie != null) {
            String token = cookie.getValue();
            if (token != null) {
                String subject = jwtService.verifyToken(token);
                User userAuth = userRepository.findByName(subject).orElseThrow(() -> new UsernameNotFoundException("User not found."));
                UsernamePasswordAuthenticationToken authenticated = UsernamePasswordAuthenticationToken.authenticated(userAuth, null, userAuth.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authenticated);
            }
        }
        filterChain.doFilter(request, response);
    }
}
