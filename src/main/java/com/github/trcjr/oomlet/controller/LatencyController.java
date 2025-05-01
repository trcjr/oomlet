package com.github.trcjr.oomlet.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/latency")
public class LatencyController {

    @GetMapping
    public ResponseEntity<String> simulateLatency(@RequestParam(defaultValue = "1000") long delayMillis) {
        try {
            Thread.sleep(delayMillis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return ResponseEntity.status(500).body("Interrupted during latency simulation.");
        }

        return ResponseEntity.ok("Responded after " + delayMillis + " ms");
    }
}