package com.github.trcjr.oomlet.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CpuBurnResponseTest {

    @Test
    void testDefaultConstructorAndSetters() {
        CpuBurnResponse response = new CpuBurnResponse();
        response.setRequestedMillis(1500L);
        response.setRequestedThreads(4);
        response.setStatus("OK");

        assertEquals(1500L, response.getRequestedMillis());
        assertEquals(4, response.getRequestedThreads());
        assertEquals("OK", response.getStatus());
    }

    @Test
    void testAllArgsConstructor() {
        CpuBurnResponse response = new CpuBurnResponse(2000L, 6, "Success");

        assertEquals(2000L, response.getRequestedMillis());
        assertEquals(6, response.getRequestedThreads());
        assertEquals("Success", response.getStatus());
    }

    @Test
    void testUnusedConstructorForCoverage() {
        CpuBurnResponse response = new CpuBurnResponse(1000L, 2, 500L, 1);
        assertNotNull(response);
    }
}
