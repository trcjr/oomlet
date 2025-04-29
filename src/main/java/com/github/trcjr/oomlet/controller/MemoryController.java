package com.github.trcjr.oomlet.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class MemoryController {

    private static final Logger logger = LoggerFactory.getLogger(MemoryController.class);

    @GetMapping("/allocate-memory")
    public Map<String, Object> allocateMemory(@RequestParam(name = "bytes") long bytes) {
        List<byte[]> allocations = new ArrayList<>();
        long allocated = 0;
        int blockSize = 1024 * 1024; // 1 MB blocks

        try {
            while (allocated < bytes) {
                int allocationSize = (int) Math.min(blockSize, bytes - allocated);
                allocations.add(new byte[allocationSize]);
                allocated += allocationSize;
                if (allocated % (blockSize * 10) == 0) {
                    logger.info("Successfully allocated {} bytes", allocated);
                }
            }
        } catch (OutOfMemoryError e) {
            logger.warn("OutOfMemoryError encountered after allocating {} bytes", allocated);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("requestedBytes", bytes);
        response.put("allocatedBytes", allocated);
        response.put("failedBytes", bytes - allocated);

        logger.info("Memory allocation request completed: requested={}, allocated={}, failed={}",
                bytes, allocated, bytes - allocated);

        // Encourage garbage collection to clean up quickly
        allocations.clear();
        System.gc();

        return response;
    }
}