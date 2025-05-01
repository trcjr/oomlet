package com.github.trcjr.oomlet.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

@RestController
public class StatusController {

    private static final Logger logger = LoggerFactory.getLogger(StatusController.class);

    @GetMapping("/api/status")
    public ResponseEntity<String> setStatus(
            @RequestParam(defaultValue = "200") int responseCode,
            @RequestParam(defaultValue = "0") long millis) {
        
        logger.info("Received request: responseCode={} millis={}", responseCode, millis);
    
        try {
            if (millis > 0) {
                Thread.sleep(millis);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("Interrupted while sleeping", e);
            return ResponseEntity.status(500).body("Interrupted while sleeping.");
        }
    
        logger.info("Returning HTTP status {}", responseCode);
        return ResponseEntity.status(responseCode)
                .body("Returning HTTP status: " + responseCode + " after " + millis + " ms");
    }
}