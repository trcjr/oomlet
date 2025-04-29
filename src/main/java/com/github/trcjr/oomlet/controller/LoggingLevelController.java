package com.github.trcjr.oomlet.controller;

import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;

import java.util.Locale;
import java.util.Map;

@RestController
@RequestMapping("/api/logging")
public class LoggingLevelController {

    private static final Logger logger = (Logger) LoggerFactory.getLogger(LoggingLevelController.class);

    @PostMapping("/spring")
    public ResponseEntity<String> setSpringLoggingLevel(@RequestParam String level) {
        Logger springLogger = (Logger) LoggerFactory.getLogger("org.springframework");
        try {
            Level newLevel = Level.valueOf(level.toUpperCase(Locale.ROOT));
            logger.info("Setting logging level for org.springframework to {} - {}", newLevel, level.toUpperCase(Locale.ROOT));
            if (newLevel.toString().toUpperCase() != level.toUpperCase(Locale.ROOT) ) {
                logger.warn("Invalid Logging level {}", level.toUpperCase(Locale.ROOT));
                throw new IllegalArgumentException("Invalid log level: " + level);
            }
            springLogger.setLevel(newLevel);
            return ResponseEntity.ok("Logging level for org.springframework set to " + newLevel);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid log level: " + level);
        }
    }

    @GetMapping("/spring")
    public ResponseEntity<Map<String, String>> getSpringLoggingLevel() {
        Logger springLogger = (Logger) LoggerFactory.getLogger("org.springframework");
        Level currentLevel = springLogger.getLevel();
        return ResponseEntity.ok(
                Map.of("org.springframework", currentLevel != null ? currentLevel.toString() : "null (inherited)"));
    }
}