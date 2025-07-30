package com.github.trcjr.oomlet.service;

import com.github.trcjr.oomlet.dto.CpuBurnResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CpuBurnServiceTest {

    @Autowired
    private CpuBurnService cpuBurnService;

    @Test
    void testBurnCpuShortDuration() {
        // Test with a very short duration to keep test fast
        long startTime = System.currentTimeMillis();
        CpuBurnResponse response = cpuBurnService.burnCpu(10, 1);
        long endTime = System.currentTimeMillis();

        assertEquals(10, response.getRequestedMillis());
        assertEquals(1, response.getRequestedThreads());
        assertEquals("completed", response.getStatus());

        // Verify it actually took some time (at least 5ms)
        assertTrue(endTime - startTime >= 5, "CPU burn should have taken some time");
    }

    @Test
    void testBurnCpuMultipleThreads() {
        long startTime = System.currentTimeMillis();
        CpuBurnResponse response = cpuBurnService.burnCpu(10, 2);
        long endTime = System.currentTimeMillis();

        assertEquals(10, response.getRequestedMillis());
        assertEquals(2, response.getRequestedThreads());
        assertEquals("completed", response.getStatus());

        // Verify it actually took some time
        assertTrue(endTime - startTime >= 5, "CPU burn should have taken some time");
    }
}
