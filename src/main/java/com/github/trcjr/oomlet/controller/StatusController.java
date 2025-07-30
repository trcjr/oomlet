package com.github.trcjr.oomlet.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.github.trcjr.oomlet.service.StatusService;

@RestController
@RequestMapping("/api/status")
public class StatusController {

    private static final Logger logger = LoggerFactory.getLogger(StatusController.class);

    private final StatusService statusService;

    @Autowired
    public StatusController(StatusService statusService) {
        this.statusService = statusService;
    }

    @GetMapping
    public ResponseEntity<String> setStatus(
            @RequestParam(defaultValue = "200") int code,
            @RequestParam(defaultValue = "0") long delayMillis) {

        try {
            String body = statusService.setStatus(code, delayMillis);
            return ResponseEntity.status(code).body(body);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e) {
            if (e.getMessage().contains("Interrupted")) {
                return ResponseEntity.status(500).body("Returning HTTP status: 500 due to interruption");
            }
            throw e;
        }
    }
}
