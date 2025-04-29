package com.github.trcjr.oomlet;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class ToggleableHealthIndicator implements HealthIndicator {
    private final HealthToggleService healthToggleService;

    public ToggleableHealthIndicator(HealthToggleService healthToggleService) {
        this.healthToggleService = healthToggleService;
    }

    @Override
    public Health health() {
        if (healthToggleService.isHealthy()) {
            return Health.up().build();
        } else {
            return Health.down().withDetail("error", "Manually toggled to unhealthy").build();
        }
    }
}