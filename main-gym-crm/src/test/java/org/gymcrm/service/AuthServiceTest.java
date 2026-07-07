package org.gymcrm.service;

import org.gymcrm.model.Trainee;
import org.gymcrm.model.User;
import org.gymcrm.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    @Test
    void testChangePassword_Success() {
        User user = new Trainee();
        user.setUsername("user");
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("newPass")).thenReturn("hashedNewPass");

        authService.changePassword("user", "newPass");

        verify(userRepository).save(user);
        verify(passwordEncoder).encode("newPass");
    }

    @Test
    void testChangePassword_UserNotFound() {
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> authService.changePassword("unknown", "newPass"));
    }
}
