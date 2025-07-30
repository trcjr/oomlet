package com.github.trcjr.oomlet.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FileHandleServiceImpl implements FileHandleService {

    private static final Logger logger = LoggerFactory.getLogger(FileHandleServiceImpl.class);

    @Override
    public Map<String, Object> openFiles(int count) {
        List<FileInputStream> openStreams = new ArrayList<>();
        int successCount = 0;

        try {
            for (int i = 0; i < count; i++) {
                try {
                    File tempFile = File.createTempFile("oomlet-fh-", ".tmp");
                    tempFile.deleteOnExit(); // Auto-clean temp files on JVM shutdown

                    FileInputStream fis = new FileInputStream(tempFile);
                    openStreams.add(fis);
                    successCount++;
                } catch (IOException e) {
                    logger.warn("Failed to open file handle at index {}: {}", i, e.getMessage());
                    break; // Stop trying after first failure (optional, can continue if you want)
                }
            }
        } finally {
            // Always close all opened file streams
            for (FileInputStream fis : openStreams) {
                try {
                    fis.close();
                } catch (IOException e) {
                    logger.warn("Failed to close file handle: {}", e.getMessage());
                }
            }
        }

        Map<String, Object> response = new HashMap<>();
        response.put("requested", count);
        response.put("successfullyOpened", successCount);
        response.put("failed", count - successCount);

        logger.info("File handle open request completed: requested={}, success={}, failed={}",
                count, successCount, count - successCount);

        return response;
    }
}
