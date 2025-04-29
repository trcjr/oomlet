package com.github.trcjr.oomlet;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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