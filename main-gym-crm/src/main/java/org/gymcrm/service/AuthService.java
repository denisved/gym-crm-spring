package org.gymcrm.service;

import lombok.RequiredArgsConstructor;
import org.gymcrm.model.User;
import org.gymcrm.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    @Transactional
    public void changePassword(String username, String newPassword) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));


        user.setPassword(passwordEncoder.encode(newPassword));

        userRepository.save(user);
    }
}