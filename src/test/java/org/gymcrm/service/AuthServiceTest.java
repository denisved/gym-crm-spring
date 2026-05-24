package org.gymcrm.service;

import org.gymcrm.model.Trainee;
import org.gymcrm.model.User;
import org.gymcrm.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AuthService authService;

    @Test
    void testAuthenticate_Success() {
        User user = new Trainee();
        user.setUsername("user");
        user.setPassword("pass");
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));

        assertTrue(authService.authenticate("user", "pass"));
    }

    @Test
    void testAuthenticate_Failure() {
        User user = new Trainee();
        user.setUsername("user");
        user.setPassword("pass");
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));

        assertFalse(authService.authenticate("user", "wrong"));
    }

    @Test
    void testAuthenticate_UserNotFound() {
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        assertFalse(authService.authenticate("unknown", "pass"));
    }
}
