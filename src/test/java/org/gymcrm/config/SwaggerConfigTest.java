package org.gymcrm.config;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class SwaggerConfigTest {

    @Test
    void contextLoads() {
        SwaggerConfig swaggerConfig = new SwaggerConfig();
        assertNotNull(swaggerConfig);
    }
}
