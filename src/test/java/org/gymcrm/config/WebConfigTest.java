package org.gymcrm.config;

import org.gymcrm.interceptor.AuthenticationInterceptor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WebConfigTest {

    @Mock
    private AuthenticationInterceptor authenticationInterceptor;

    @InjectMocks
    private WebConfig webConfig;

    @Test
    void addInterceptors_AddsInterceptors() {
        InterceptorRegistry registry = mock(InterceptorRegistry.class);
        org.springframework.web.servlet.config.annotation.InterceptorRegistration registration = mock(org.springframework.web.servlet.config.annotation.InterceptorRegistration.class, RETURNS_DEEP_STUBS);

        when(registry.addInterceptor(any())).thenReturn(registration);
        when(registration.addPathPatterns(anyString())).thenReturn(registration);

        webConfig.addInterceptors(registry);

        verify(registry, times(2)).addInterceptor(any());
    }
}
