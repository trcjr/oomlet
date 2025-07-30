package com.github.trcjr.oomlet.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MemoryServiceImpl implements MemoryService {

    private static final Logger logger = LoggerFactory.getLogger(MemoryServiceImpl.class);

    @Override
    public Map<String, Object> allocateMemory(long bytes) {
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
