package com.github.trcjr.oomlet.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class StatusServiceTest {

    @Autowired
    private StatusService statusService;

    @Test
    void testSetStatusWithZeroDelay() {
        String result = statusService.setStatus(200, 0);
        assertEquals("Returning HTTP status: 200 after 0 ms", result);
    }

    @Test
    void testSetStatusWithShortDelay() {
        long startTime = System.currentTimeMillis();
        String result = statusService.setStatus(404, 5);
        long endTime = System.currentTimeMillis();

        assertEquals("Returning HTTP status: 404 after 5 ms", result);
        assertTrue(endTime - startTime >= 5, "Should have taken at least 5ms");
    }

    @Test
    void testSetStatusWithInvalidStatusCode() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            statusService.setStatus(999, 0);
        });
        assertTrue(exception.getMessage().contains("Invalid status code"));
    }

    @Test
    void testSetStatusWithNegativeDelay() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            statusService.setStatus(200, -1);
        });
        assertTrue(exception.getMessage().contains("Invalid delay"));
    }

    @Test
    void testSetStatusWithTooLargeDelay() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            statusService.setStatus(200, 999999999);
        });
        assertTrue(exception.getMessage().contains("Invalid delay"));
    }
}
