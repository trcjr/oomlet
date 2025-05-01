package com.github.trcjr.oomlet.controller;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

class LatencyControllerTest {

    @Test
    void simulateLatency_returnsResponseAfterDelay() {
        LatencyController controller = new LatencyController();
        long delay = 200;
        long start = System.currentTimeMillis();

        ResponseEntity<String> response = controller.simulateLatency(delay);

        long elapsed = System.currentTimeMillis() - start;
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Responded after " + delay + " ms", response.getBody());
        assertTrue(elapsed >= delay);
    }

    @Test
    void simulateLatency_handlesInterruptionGracefully() {
        LatencyController controller = new LatencyController() {
            @Override
            public ResponseEntity<String> simulateLatency(long delayMillis) {
                Thread.currentThread().interrupt(); // force interruption
                return super.simulateLatency(delayMillis);
            }
        };

        ResponseEntity<String> response = controller.simulateLatency(100);
        assertEquals(500, response.getStatusCodeValue());
        assertEquals("Interrupted during latency simulation.", response.getBody());
    }
}