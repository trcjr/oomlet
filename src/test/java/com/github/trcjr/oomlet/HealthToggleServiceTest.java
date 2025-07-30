package com.github.trcjr.oomlet;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class HealthToggleServiceTest {

    @Test
    void testToggleHealth() {
        HealthToggleService service = new HealthToggleService();
        service.setHealthy(false);
        assertFalse(service.isHealthy());
        service.setHealthy(true);
        assertTrue(service.isHealthy());
    }
}
