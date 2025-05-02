package com.github.trcjr.oomlet.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

@RestController
public class UlimitController {

    private static final Logger logger = LoggerFactory.getLogger(UlimitController.class);

    private final Supplier<Process> processSupplier;

    public UlimitController() {
        this(() -> {
            try {
                return new ProcessBuilder("sh", "-c", "ulimit -a").start();
            } catch (Exception e) {
                throw new RuntimeException("Failed to start ulimit process", e);
            }
        });
    }

    public UlimitController(Supplier<Process> processSupplier) {
        this.processSupplier = processSupplier;
    }

    @GetMapping("/api/ulimits")
    public ResponseEntity<?> getLimits() {
        try {
            Process process = processSupplier.get();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                StringBuilder output = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }

                Map<String, String> limits = parseUlimitOutput(output.toString());

                if (limits.isEmpty()) {
                    throw new IllegalStateException("Failed to parse ulimit output");
                }

                return ResponseEntity.ok(limits);
            }
        } catch (Exception e) {
            logger.error("Failed to get ulimit info", e);
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    protected Map<String, String> parseUlimitOutput(String output) {
        Map<String, String> limits = new LinkedHashMap<>();

        for (String line : output.split("\n")) {
            line = line.strip();
            if (line.isEmpty()) continue;

            String[] parts = line.split("\\)\\s+", 2);
            if (parts.length < 2 || !line.contains("(")) continue;

            // Extract the name from before the parentheses
            String name = line.substring(0, line.indexOf('(')).trim();
            name = name.toLowerCase().replaceAll("[^a-z0-9]+", "_");

            limits.put(name, parts[1].trim());
        }

        return limits;
    }
}