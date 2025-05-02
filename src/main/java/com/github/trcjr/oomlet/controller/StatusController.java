package com.github.trcjr.oomlet.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/status")
public class StatusController {

    private static final Logger logger = LoggerFactory.getLogger(StatusController.class);

    private static final int MAX_STATUS = 599;
    private static final int MIN_STATUS = 100;
    private static final long MAX_DELAY_MS = 30_000;

    @GetMapping
    public ResponseEntity<String> setStatus(
            @RequestParam(defaultValue = "200") int code,
            @RequestParam(defaultValue = "0") long delayMillis) {

        logger.info("Received status request: code={}, delay={}ms", code, delayMillis);

        if (code < MIN_STATUS || code > MAX_STATUS) {
            logger.warn("Invalid status code: {}. Must be between {} and {}.", code, MIN_STATUS, MAX_STATUS);
            return ResponseEntity.badRequest()
                    .body("Invalid status code. Must be between " + MIN_STATUS + " and " + MAX_STATUS + ".");
        }

        if (delayMillis < 0) {
            logger.warn("Negative delay: {}ms. Delay must be >= 0.", delayMillis);
            return ResponseEntity.badRequest()
                    .body("Invalid delay. Must be >= 0.");
        }

        if (delayMillis > MAX_DELAY_MS) {
            logger.warn("Delay too long: {}ms. Max allowed is {}ms.", delayMillis, MAX_DELAY_MS);
            return ResponseEntity.badRequest()
                    .body("Invalid delay. Max allowed is " + MAX_DELAY_MS + " ms.");
        }

        try {
            if (delayMillis > 0) {
                Thread.sleep(delayMillis);
            }

            String body = "Returning HTTP status: " + code + " after " + delayMillis + " ms";
            return ResponseEntity.status(code).body(body);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            String body = "Returning HTTP status: 500 due to interruption";
            logger.error("Interrupted during delay", e);
            return ResponseEntity.status(500).body(body);
        }
    }
}