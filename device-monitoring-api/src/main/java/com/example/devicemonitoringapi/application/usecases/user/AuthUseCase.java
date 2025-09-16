package com.example.devicemonitoringapi.application.usecases.user;

import com.example.devicemonitoringapi.application.services.JwtService;
import com.example.devicemonitoringapi.domain.models.User;
import com.example.devicemonitoringapi.dtos.UserAuthDTO;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class AuthUseCase {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthUseCase(AuthenticationManager authenticationManager, JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    public String execute(UserAuthDTO userAuthDTO) {
        var authenticationToken = new UsernamePasswordAuthenticationToken(userAuthDTO.name(), userAuthDTO.password());
        Authentication userAuth = authenticationManager.authenticate(authenticationToken);
        return jwtService.generateToken(((User) userAuth.getPrincipal()).getUsername());

    }
}
