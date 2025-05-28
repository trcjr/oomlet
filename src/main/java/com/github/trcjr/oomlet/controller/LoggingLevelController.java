package com.github.trcjr.oomlet.controller;

import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;
import org.springframework.boot.logging.LogLevel;
import org.springframework.boot.logging.LoggingSystem;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/logging")
public class LoggingLevelController {

    private final LoggingSystem loggingSystem;
    private static final String PACKAGE = "org.springframework";
    private static final Set<String> VALID_LEVELS = Set.of("TRACE", "DEBUG", "INFO", "WARN", "ERROR", "FATAL", "OFF");

    public LoggingLevelController(LoggingSystem loggingSystem) {
        this.loggingSystem = loggingSystem;
    }

    @GetMapping("/spring")
    public Map<String, String> getSpringLoggingLevel() {
        LogLevel level = loggingSystem.getLoggerConfiguration(PACKAGE).getEffectiveLevel();
        return Map.of(PACKAGE, level.name());
    }

    @PostMapping("/spring")
    public ResponseEntity<String> setSpringLoggingLevel(@RequestParam String level) {
        if (!VALID_LEVELS.contains(level.toUpperCase())) {
            return ResponseEntity.badRequest().body("Invalid log level: " + level);
        }

        loggingSystem.setLogLevel(PACKAGE, LogLevel.valueOf(level.toUpperCase()));
        return ResponseEntity.ok("Logging level for " + PACKAGE + " set to " + level.toUpperCase());
    }
}
