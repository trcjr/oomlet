package com.github.trcjr.oomlet.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/latency")
public class LatencyController {

    private final Sleeper sleeper;

    public LatencyController() {
        this(Thread::sleep);
    }

    // For testability
    LatencyController(Sleeper sleeper) {
        this.sleeper = sleeper;
    }

    @GetMapping
    public ResponseEntity<String> simulateLatency(@RequestParam(defaultValue = "1000") long delayMillis) {
        try {
            sleeper.sleep(delayMillis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return ResponseEntity.status(500).body("Interrupted during latency simulation.");
        }

        return ResponseEntity.ok("Responded after " + delayMillis + " ms");
    }

    @FunctionalInterface
    public interface Sleeper {
        void sleep(long millis) throws InterruptedException;
    }
}
