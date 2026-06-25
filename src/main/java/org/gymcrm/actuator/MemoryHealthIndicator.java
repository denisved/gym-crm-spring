package org.gymcrm.actuator;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class MemoryHealthIndicator implements HealthIndicator {

    private static final double MIN_FREE_MEMORY_PERCENTAGE = 10.0;

    @Override
    public Health health() {
        long freeMemory = Runtime.getRuntime().freeMemory();
        long totalMemory = Runtime.getRuntime().totalMemory();
        long maxMemory = Runtime.getRuntime().maxMemory();

        double freeMemoryPercent = ((double) freeMemory / (double) totalMemory) * 100;

        String freeMb = freeMemory / (1024 * 1024) + " MB";
        String totalMb = totalMemory / (1024 * 1024) + " MB";
        String maxMb = maxMemory / (1024 * 1024) + " MB";

        if (freeMemoryPercent >= MIN_FREE_MEMORY_PERCENTAGE) {
            return Health.up()
                    .withDetail("status", "Memory is sufficient")
                    .withDetail("free_memory", freeMb)
                    .withDetail("total_memory", totalMb)
                    .withDetail("max_memory", maxMb)
                    .withDetail("free_memory_percent", String.format("%.2f%%", freeMemoryPercent))
                    .build();
        } else {
            return Health.down()
                    .withDetail("status", "CRITICAL: Memory is running low!")
                    .withDetail("free_memory", freeMb)
                    .withDetail("total_memory", totalMb)
                    .withDetail("max_memory", maxMb)
                    .withDetail("free_memory_percent", String.format("%.2f%%", freeMemoryPercent))
                    .build();
        }
    }
}