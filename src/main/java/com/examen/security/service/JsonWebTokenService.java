package com.examen.security.service;

import org.springframework.security.core.userdetails.UserDetails;

public interface JsonWebTokenService {
    String extractUsername(String token);
    String generateToken(UserDetails userDetails);
    boolean validateToken(String token, UserDetails userDetails);
}
