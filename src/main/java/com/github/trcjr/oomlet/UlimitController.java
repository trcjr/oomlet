package com.github.trcjr.oomlet;

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

    private final Supplier<ProcessBuilder> processBuilderSupplier;

    public UlimitController() {
        this(() -> new ProcessBuilder("bash", "-c", "ulimit -a"));
    }

    UlimitController(Supplier<ProcessBuilder> processBuilderSupplier) {
        this.processBuilderSupplier = processBuilderSupplier;
    }

    @GetMapping("/ulimits")
    public ResponseEntity<Map<String, String>> getUlimits() {
        Map<String, String> limits = new LinkedHashMap<>();

        try {
            ProcessBuilder builder = processBuilderSupplier.get();
            builder.redirectErrorStream(true);
            Process process = builder.start();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    if (!line.isEmpty()) {
                        String[] parts = line.split("\\s+\\(.*?\\)\\s+");
                        if (parts.length == 2) {
                            limits.put(parts[0].trim(), parts[1].trim());
                        }
                    }
                }
            }
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                logger.warn("ulimit command exited with non-zero code: {}", exitCode);
                return ResponseEntity.status(500).body(Map.of("error", "Failed to fetch ulimits."));
            }
        } catch (Exception e) {
            logger.error("Failed to execute ulimit command", e);
            return ResponseEntity.status(500).body(Map.of("error", "Exception occurred: " + e.getMessage()));
        }

        return ResponseEntity.ok(limits);
    }
}