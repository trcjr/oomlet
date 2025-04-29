package com.github.trcjr.oomlet.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.trcjr.oomlet.HealthToggleService;

@RestController
@RequestMapping("/api/health-toggle")
public class HealthToggleController {
    private final HealthToggleService healthToggleService;

    public HealthToggleController(HealthToggleService healthToggleService) {
        this.healthToggleService = healthToggleService;
    }

    @PostMapping("/enable")
    public String enableHealth() {
        healthToggleService.setHealthy(true);
        return "Health set to UP";
    }

    @PostMapping("/disable")
    public String disableHealth() {
        healthToggleService.setHealthy(false);
        return "Health set to DOWN";
    }
    
}