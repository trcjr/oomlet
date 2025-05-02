package com.github.trcjr.oomlet.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

@RestController
@RequestMapping("/api")
public class UlimitController {

    private static final Logger logger = LoggerFactory.getLogger(UlimitController.class);
    private final Supplier<Process> processSupplier;

    public UlimitController() {
        this(() -> {
            ProcessBuilder pb = new ProcessBuilder("bash", "-c", "ulimit -a");
            pb.redirectErrorStream(true);
            try {
                return pb.start();
            } catch (Exception e) {
                throw new RuntimeException("Failed to start ulimit process", e);
            }
        });
    }

    public UlimitController(Supplier<Process> processSupplier) {
        this.processSupplier = processSupplier;
    }

    @GetMapping("/ulimits")
    public ResponseEntity<Map<String, String>> getUlimits() {
        Map<String, String> limits = new LinkedHashMap<>();
        try {
            Process process = processSupplier.get();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    if (!line.isEmpty()) {
                        String[] parts = line.split("\\s+\\(.*?\\)\\s+");
                        if (parts.length == 2) {
                            limits.put(parts[0].trim().replaceAll("\\s+", "_"), parts[1].trim());
                        }
                    }
                }
            }

            int exitCode = process.waitFor();
            if (exitCode != 0) {
                logger.warn("ulimit command exited with non-zero code: {}", exitCode);
                return ResponseEntity.status(500).body(Map.of("error", "Failed to fetch ulimits."));
            }

            return ResponseEntity.ok(limits);

        } catch (Exception e) {
            logger.error("Failed to execute ulimit command", e);
            return ResponseEntity.status(500).body(Map.of("error", "Exception occurred: " + e.getMessage()));
        }
    }
}