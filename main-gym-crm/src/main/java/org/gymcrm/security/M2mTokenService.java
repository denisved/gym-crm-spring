package org.gymcrm.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

@Service
public class M2mTokenService {

    @Value("${app.security.m2m-secret}")
    private String m2mSecret;

    public String generateSystemToken() {
        SecretKey key = Keys.hmacShaKeyFor(m2mSecret.getBytes(StandardCharsets.UTF_8));

        long jwtExpirationMs = 600000;

        return Jwts.builder()
                .subject("main-gym-service")
                .claims(Map.of("roles", "ROLE_SYSTEM"))
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(key, Jwts.SIG.HS256)
                .compact();
    }
}