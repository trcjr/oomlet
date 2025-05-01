package com.github.trcjr.oomlet.controller;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

class CrashControllerTest {

    @Test
    void crash_returnsResponseWithExitCode() {
        CrashController controller = new CrashController();
        ResponseEntity<String> response = controller.crash(137);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Crashing with exit code: 137", response.getBody());
    }

    @Test
    void crash_usesDefaultExitCodeWhenMissing() {
        CrashController controller = new CrashController();
        ResponseEntity<String> response = controller.crash(1);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Crashing with exit code: 1", response.getBody());
    }
}