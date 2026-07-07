package org.gymcrm.security;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfigurationSource;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

class SecurityConfigTest {

    @Test
    void testBeans() {
        JwtAuthenticationFilter filter = mock(JwtAuthenticationFilter.class);
        SecurityConfig config = new SecurityConfig(filter);
        
        PasswordEncoder encoder = config.passwordEncoder();
        assertNotNull(encoder);
        
        CorsConfigurationSource cors = config.corsConfigurationSource();
        assertNotNull(cors);
    }
}
