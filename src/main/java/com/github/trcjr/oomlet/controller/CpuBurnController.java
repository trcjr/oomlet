package com.github.trcjr.oomlet.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.github.trcjr.oomlet.dto.CpuBurnResponse;
import com.github.trcjr.oomlet.service.CpuBurnService;

@RestController
public class CpuBurnController {

    private static final Logger logger = LoggerFactory.getLogger(CpuBurnController.class);

    private final CpuBurnService cpuBurnService;

    @Autowired
    public CpuBurnController(CpuBurnService cpuBurnService) {
        this.cpuBurnService = cpuBurnService;
    }

    @GetMapping("/api/burn-cpu")
    public ResponseEntity<CpuBurnResponse> burnCpu(
            @RequestParam(defaultValue = "1000") long millis,
            @RequestParam(defaultValue = "1") int threads) {

        logger.info("Received CPU burn request: millis={} threads={}", millis, threads);

        CpuBurnResponse response = cpuBurnService.burnCpu(millis, threads);

        if ("interrupted".equals(response.getStatus())) {
            return ResponseEntity.status(500).body(response);
        }

        return ResponseEntity.ok(response);
    }
}
