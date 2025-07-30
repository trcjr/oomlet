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
}
