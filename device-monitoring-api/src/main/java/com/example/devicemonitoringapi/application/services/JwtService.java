package com.example.devicemonitoringapi.application.services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class JwtService {
    @Value("${spring.application.name}")
    private String nameApplication;

    public String generateToken(String username) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(nameApplication);
            return JWT.create()
                    .withIssuer(nameApplication)
                    .withSubject(username)
                    .withExpiresAt(Instant.now().plusSeconds(500000))
                    .sign(algorithm);
        } catch (JWTCreationException exception) {
            throw new RuntimeException(exception);
        }
    }

    public String verifyToken(String token) {
        Algorithm algorithm = Algorithm.HMAC256(nameApplication);
        return JWT.require(algorithm)
                .withIssuer(nameApplication)
                .build()
                .verify(token)
                .getSubject();

    }
}
