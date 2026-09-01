package com.github.trcjr.oomlet.controller;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

class LatencyControllerUnitTest {

    @Test
    void simulateLatency_returnsExpectedMessage() {
        LatencyController controller = new LatencyController(millis -> { /* no-op */ });
        ResponseEntity<String> response = controller.simulateLatency(123);
        assertEquals(200, response.getStatusCode().value());
        assertEquals("Responded after 123 ms", response.getBody());
    }

    @Test
    void simulateLatency_handlesInterruption() {
        LatencyController controller = new LatencyController(millis -> { throw new InterruptedException(); });
        ResponseEntity<String> response = controller.simulateLatency(456);
        assertEquals(500, response.getStatusCode().value());
        assertEquals("Interrupted during latency simulation.", response.getBody());
    }
}
