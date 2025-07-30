package com.github.trcjr.oomlet.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MemoryServiceTest {

    @Autowired
    private MemoryService memoryService;

    @Test
    void testAllocateMemoryZero() {
        Map<String, Object> result = memoryService.allocateMemory(0);

        assertEquals(0L, result.get("requestedBytes"));
        assertEquals(0L, result.get("allocatedBytes"));
        assertEquals(0L, result.get("failedBytes"));
    }

    @Test
    void testAllocateMemorySmall() {
        Map<String, Object> result = memoryService.allocateMemory(1024); // 1KB

        assertEquals(1024L, result.get("requestedBytes"));
        assertEquals(1024L, result.get("allocatedBytes"));
        assertEquals(0L, result.get("failedBytes"));
    }

    @Test
    void testAllocateMemoryMedium() {
        Map<String, Object> result = memoryService.allocateMemory(1024 * 1024); // 1MB

        assertEquals(1024 * 1024L, result.get("requestedBytes"));
        assertEquals(1024 * 1024L, result.get("allocatedBytes"));
        assertEquals(0L, result.get("failedBytes"));
    }

    @Test
    void testAllocateMemoryLarge() {
        // Test with a larger allocation to potentially trigger OutOfMemoryError
        // Note: This test may not reliably trigger OOM on all systems
        Map<String, Object> result = memoryService.allocateMemory(50 * 1024 * 1024); // 50MB

        assertEquals(50 * 1024 * 1024L, result.get("requestedBytes"));
        // The allocated bytes should be <= requested bytes
        assertTrue((Long) result.get("allocatedBytes") <= 50 * 1024 * 1024L);
        // Failed bytes should be >= 0
        assertTrue((Long) result.get("failedBytes") >= 0L);
    }

    @Test
    void testAllocateMemoryVeryLarge() {
        // Test with a very large allocation that might trigger OOM
        // This is more likely to trigger the OutOfMemoryError path
        Map<String, Object> result = memoryService.allocateMemory(100 * 1024 * 1024); // 100MB

        assertEquals(100 * 1024 * 1024L, result.get("requestedBytes"));
        // Should handle the allocation gracefully even if OOM occurs
        assertTrue((Long) result.get("allocatedBytes") >= 0L);
        assertTrue((Long) result.get("failedBytes") >= 0L);
    }
}
