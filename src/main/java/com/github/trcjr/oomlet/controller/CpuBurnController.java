package com.github.trcjr.oomlet.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

import com.github.trcjr.oomlet.dto.CpuBurnResponse;

@RestController
public class CpuBurnController {

    private static final Logger logger = LoggerFactory.getLogger(CpuBurnController.class);

    @GetMapping("/api/burn-cpu")
    public ResponseEntity<CpuBurnResponse> burnCpu(
            @RequestParam(defaultValue = "1000") long millis,
            @RequestParam(defaultValue = "1") int threads) {

        logger.info("Received CPU burn request: millis={} threads={}", millis, threads);

        long endTime = System.currentTimeMillis() + millis;
        List<Thread> workers = new ArrayList<>();

        for (int i = 0; i < threads; i++) {
            Thread worker = new Thread(() -> {
                while (System.currentTimeMillis() < endTime) {
                    double x = Math.random() * Math.random(); // Do something to waste CPU
                }
            });
            worker.start();
            workers.add(worker);
        }

        // Optionally wait for all threads to finish
        for (Thread worker : workers) {
            try {
                worker.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.error("CPU burn interrupted", e);
                return ResponseEntity.status(500).body(
                    new CpuBurnResponse(millis, threads, "interrupted")
                );
            }
        }

        logger.info("CPU burn complete");

        CpuBurnResponse response = new CpuBurnResponse(millis, threads, "completed");
        return ResponseEntity.ok(response);
    }
}
