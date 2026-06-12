package org.gymcrm.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilsTest {

    private JwtUtils jwtUtils;
    private final String secret = "mysecretkeymustbelongenoughforhmacsha256algorithm";
    private final int expirationMs = 3600000;

    @BeforeEach
    void setUp() {
        jwtUtils = new JwtUtils();
        ReflectionTestUtils.setField(jwtUtils, "jwtSecret", secret);
        ReflectionTestUtils.setField(jwtUtils, "jwtExpirationMs", expirationMs);
    }

    @Test
    void testGenerateAndExtractToken() {
        String username = "testuser";
        String token = jwtUtils.generateToken(username);
        assertNotNull(token);
        
        String extractedUsername = jwtUtils.extractUsername(token);
        assertEquals(username, extractedUsername);
    }

    @Test
    void testValidateToken_Success() {
        String token = jwtUtils.generateToken("user");
        assertTrue(jwtUtils.validateToken(token));
    }

    @Test
    void testValidateToken_Failure() {
        assertFalse(jwtUtils.validateToken("invalid.token.here"));
    }

    @Test
    void testValidateToken_Expired() {
        ReflectionTestUtils.setField(jwtUtils, "jwtExpirationMs", -1000);
        String token = jwtUtils.generateToken("user");
        assertFalse(jwtUtils.validateToken(token));
    }
}
