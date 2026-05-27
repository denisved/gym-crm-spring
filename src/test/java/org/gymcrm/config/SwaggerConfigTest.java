package org.gymcrm.config;

import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class SwaggerConfigTest {

    private final SwaggerConfig swaggerConfig = new SwaggerConfig();

    @Test
    void customOpenAPI_ReturnsOpenAPI() {
        assertNotNull(swaggerConfig.customOpenAPI());
    }

    @Test
    void configProperties_ReturnsProperties() {
        assertNotNull(swaggerConfig.springDocConfigProperties());
        assertNotNull(swaggerConfig.swaggerUiConfigProperties());
        assertNotNull(swaggerConfig.swaggerUiOAuthProperties());
    }

    @Test
    void addResourceHandlers_AddsHandlers() {
        ResourceHandlerRegistry registry = mock(ResourceHandlerRegistry.class);
        when(registry.addResourceHandler(anyString())).thenReturn(mock(org.springframework.web.servlet.config.annotation.ResourceHandlerRegistration.class));

        swaggerConfig.addResourceHandlers(registry);

        verify(registry).addResourceHandler("/webjars/**");
    }
}
