package com.github.trcjr.oomlet.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/crash")
public class CrashController {

    private static final Logger logger = LoggerFactory.getLogger(CrashController.class);

    @PostMapping
    public ResponseEntity<String> crash(@RequestParam(defaultValue = "1") int code) {
        if (code < 0 || code > 255) {
            logger.warn("Invalid exit code received: {}", code);
            return ResponseEntity.badRequest().body("Invalid exit code.");
        }

        logger.warn("Crashing application with exit code: {}", code);
        shutdownWithCode(code);
        return ResponseEntity.ok("Crashing with exit code: " + code);
    }

    /**
     * Separated for easier testing (can be mocked to avoid real System.exit).
     */
    protected void shutdownWithCode(int code) {
        System.exit(code);
    }
}
