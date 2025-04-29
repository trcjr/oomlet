package com.github.trcjr.oomlet;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.boot.actuate.health.Health;

class ToggleableHealthIndicatorTest {

    @Test
    void testHealthUp() {
        HealthToggleService service = new HealthToggleService();
        service.setHealthy(true);
        ToggleableHealthIndicator indicator = new ToggleableHealthIndicator(service);
        Health health = indicator.health();
        assertThat(health.getStatus().getCode()).isEqualTo("UP");
    }

    @Test
    void testHealthDown() {
        HealthToggleService service = new HealthToggleService();
        service.setHealthy(false);
        ToggleableHealthIndicator indicator = new ToggleableHealthIndicator(service);
        Health health = indicator.health();
        assertThat(health.getStatus().getCode()).isEqualTo("DOWN");
    }
}