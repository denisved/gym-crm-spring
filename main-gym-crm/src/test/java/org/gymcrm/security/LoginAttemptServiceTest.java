package org.gymcrm.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LoginAttemptServiceTest {

    private LoginAttemptService loginAttemptService;

    @BeforeEach
    void setUp() {
        loginAttemptService = new LoginAttemptService();
    }

    @Test
    void testLoginSucceeded_ClearsAttempts() {
        String username = "user";
        loginAttemptService.loginFailed(username);
        loginAttemptService.loginSucceeded(username);
        assertFalse(loginAttemptService.isBlocked(username));
    }

    @Test
    void testLoginFailed_BlocksAfterMaxAttempts() {
        String username = "user";
        loginAttemptService.loginFailed(username);
        loginAttemptService.loginFailed(username);
        assertFalse(loginAttemptService.isBlocked(username));
        
        loginAttemptService.loginFailed(username);
        assertTrue(loginAttemptService.isBlocked(username));
    }

    @Test
    void testIsBlocked_Expires() throws InterruptedException {
        String username = "user";
        loginAttemptService.loginFailed(username);
        loginAttemptService.loginFailed(username);
        loginAttemptService.loginFailed(username);
        assertTrue(loginAttemptService.isBlocked(username));
    }
}
