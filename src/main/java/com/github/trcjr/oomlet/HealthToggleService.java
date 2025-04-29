package com.github.trcjr.oomlet;

import org.springframework.stereotype.Service;

@Service
public class HealthToggleService {
    private volatile boolean healthy = true;

    public void setHealthy(boolean healthy) {
        this.healthy = healthy;
    }

    public boolean isHealthy() {
        return healthy;
    }
}