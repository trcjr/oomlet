package com.github.trcjr.oomlet.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

class StatusControllerTest {

    private StatusController controller;

    @BeforeEach
    void setUp() {
        controller = new StatusController();
    }

    @Test
    void testSetStatus200WithZeroDelay() {
        ResponseEntity<String> response = controller.setStatus(200, 0);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Returning HTTP status: 200 after 0 ms", response.getBody());
    }

    @Test
    void testSetStatus404WithZeroDelay() {
        ResponseEntity<String> response = controller.setStatus(404, 0);
        assertEquals(404, response.getStatusCodeValue());
        assertEquals("Returning HTTP status: 404 after 0 ms", response.getBody());
    }

    @Test
    void testSetStatus500WithDelay() {
        long delay = 100L;
        long start = System.currentTimeMillis();
        ResponseEntity<String> response = controller.setStatus(500, delay);
        long end = System.currentTimeMillis();

        assertEquals(500, response.getStatusCodeValue());
        assertEquals("Returning HTTP status: 500 after 100 ms", response.getBody());

        assertTrue((end - start) >= delay, "Delay should be respected");
    }
} 
