package com.examen.security.service.impl;

import com.examen.security.aggregates.constants.Constants;
import com.examen.security.service.JsonWebTokenService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JsonWebTokenImpl implements JsonWebTokenService {

    @Value("${key.signature}")
    private String keySignature;


    @Override
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    @Override
    public String generateToken(UserDetails userDetails) {
        return Jwts.builder()
                .claim("ROLE", userDetails.getAuthorities()) // Forma de añadir claims personalizados, estos son campos que existen dentro del payolad
                .setSubject(userDetails.getUsername()) // Establece el user que se registrará en el payload del token
                .setIssuedAt(new Date(System.currentTimeMillis())) // Establece en que tiempo fue creado el token
                .setExpiration(new Date(System.currentTimeMillis() + 1200000)) // Establece la expiración del token
                .signWith(getSignKey(), SignatureAlgorithm.HS256) // Establecemos nuestra firma
                .compact();
    }

    @Override
    public boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())
                && !isTokenExpired(token));
    }

    // Generar una firma a partir de mi clave secreta
    private Key getSignKey(){
        byte[] key = Decoders.BASE64.decode(keySignature);
        return Keys.hmacShaKeyFor(key);
    }

    // Método para extraer el payload(claims) del token
    private Claims extractAllClaims(String token){
        return Jwts.parserBuilder().setSigningKey(getSignKey()).build()
                .parseClaimsJws(token).getBody();
    }

    // Método para extraer un claim en particular
    private <T> T extractClaim(String token, Function<Claims,T> claimResult){
        final Claims claims = extractAllClaims(token);
        return claimResult.apply(claims);
    }

    // Verifica que el token no haya expirado
    private boolean isTokenExpired(String token){
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }

    private Map<String,Object> addClaim(UserDetails userDetails){
        Map<String,Object> claims = new HashMap<>();
        claims.put(Constants.CLAVE_AccountNonLocked, userDetails.isAccountNonLocked());
        claims.put(Constants.CLAVE_AccountNonExpired, userDetails.isAccountNonExpired());
        claims.put(Constants.CLAVE_CredentialsNonExpired, userDetails.isCredentialsNonExpired());
        claims.put(Constants.CLAVE_Enabled, userDetails.isEnabled());
        return claims;
    }
}
