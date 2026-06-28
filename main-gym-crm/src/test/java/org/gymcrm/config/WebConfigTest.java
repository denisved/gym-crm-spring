package org.gymcrm.config;

import org.gymcrm.interceptor.TransactionInterceptor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WebConfigTest {

    @InjectMocks
    private WebConfig webConfig;

    @Test
    void addInterceptors_AddsInterceptors() {
        InterceptorRegistry registry = mock(InterceptorRegistry.class);
        InterceptorRegistration registration = mock(InterceptorRegistration.class);

        when(registry.addInterceptor(any(TransactionInterceptor.class))).thenReturn(registration);
        when(registration.addPathPatterns(anyString())).thenReturn(registration);

        webConfig.addInterceptors(registry);

        verify(registry, times(1)).addInterceptor(any(TransactionInterceptor.class));
    }
}
