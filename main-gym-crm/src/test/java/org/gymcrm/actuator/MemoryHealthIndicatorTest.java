package org.gymcrm.actuator;

import org.junit.jupiter.api.Test;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MemoryHealthIndicatorTest {

    @Test
    void testHealth() {
        MemoryHealthIndicator indicator = new MemoryHealthIndicator();
        Health health = indicator.health();
        
        assertNotNull(health);
        assertTrue(health.getStatus() == Status.UP || health.getStatus() == Status.DOWN);
        assertNotNull(health.getDetails().get("free_memory"));
        assertNotNull(health.getDetails().get("total_memory"));
    }
}
