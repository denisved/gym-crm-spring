package org.gymcrm.workload.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class M2mJwtUtilsTest {

    private M2mJwtUtils jwtUtils;
    private final String secret = "my-super-secret-key-for-m2m-jwt-token-validation";

    @BeforeEach
    void setUp() {
        jwtUtils = new M2mJwtUtils();
        ReflectionTestUtils.setField(jwtUtils, "m2mSecret", secret);
    }

    private String createToken(String role, long expirationMillis) {
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        return Jwts.builder()
                .claim("roles", role)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationMillis))
                .signWith(key)
                .compact();
    }

    @Test
    void validateToken_WithValidToken_ShouldReturnTrue() {
        String token = createToken("ROLE_SYSTEM", 100000);
        assertTrue(jwtUtils.validateToken(token));
    }

    @Test
    void validateToken_WithInvalidToken_ShouldReturnFalse() {
        String token = "invalid.token.here";
        assertFalse(jwtUtils.validateToken(token));
    }

    @Test
    void validateToken_WithExpiredToken_ShouldReturnFalse() {
        String token = createToken("ROLE_SYSTEM", -1000); 
        assertFalse(jwtUtils.validateToken(token));
    }

    @Test
    void getRoleFromToken_WithValidToken_ShouldReturnRole() {
        String token = createToken("ROLE_SYSTEM", 100000);
        String role = jwtUtils.getRoleFromToken(token);
        assertEquals("ROLE_SYSTEM", role);
    }
}
