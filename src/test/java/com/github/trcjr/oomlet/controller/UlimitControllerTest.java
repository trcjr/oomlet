package com.github.trcjr.oomlet.controller;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Map;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;

class UlimitControllerTest {

    @Test
    void testGetUlimitsReturnsSuccess() throws Exception {
        String output = "max user processes            (nproc)              12345\n"
                      + "open files                    (nofile)             67890\n";

        Supplier<Process> mockSupplier = () -> new MockProcess(output, 0);
        UlimitController controller = new UlimitController(mockSupplier);

        ResponseEntity<Map<String, String>> response = controller.getUlimits();

        assertEquals(200, response.getStatusCodeValue());
        Map<String, String> body = response.getBody();
        assertNotNull(body);
        assertEquals("12345", body.get("max_user_processes"));
        assertEquals("67890", body.get("open_files"));
    }

    @Test
    void testGetUlimitsReturnsFailureOnNonZeroExit() throws Exception {
        Supplier<Process> mockSupplier = () -> new MockProcess("", 1);
        UlimitController controller = new UlimitController(mockSupplier);

        ResponseEntity<Map<String, String>> response = controller.getUlimits();

        assertEquals(500, response.getStatusCodeValue());
        assertTrue(response.getBody().get("error").contains("Failed to fetch ulimits"));
    }

    @Test
    void testGetUlimitsReturnsFailureOnException() {
        Supplier<Process> throwingSupplier = () -> {
            throw new RuntimeException("Simulated failure");
        };
        UlimitController controller = new UlimitController(throwingSupplier);

        ResponseEntity<Map<String, String>> response = controller.getUlimits();

        assertEquals(500, response.getStatusCodeValue());
        assertTrue(response.getBody().get("error").contains("Exception occurred"));
    }

    static class MockProcess extends Process {
        private final InputStream inputStream;
        private final int exitCode;

        MockProcess(String output, int exitCode) {
            this.inputStream = new ByteArrayInputStream(output.getBytes());
            this.exitCode = exitCode;
        }

        @Override public InputStream getInputStream() { return inputStream; }
        @Override public InputStream getErrorStream() { return InputStream.nullInputStream(); }
        @Override public java.io.OutputStream getOutputStream() { return java.io.OutputStream.nullOutputStream(); }
        @Override public int waitFor() { return exitCode; }
        @Override public int exitValue() { return exitCode; }
        @Override public void destroy() {}
    }
}