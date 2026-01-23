package com.github.trcjr.oomlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@Component
public class EndpointHealthIndicator implements HealthIndicator {

    private static final Logger logger = LoggerFactory.getLogger(EndpointHealthIndicator.class);
    private final RestTemplate restTemplate;
    private final Supplier<List<HttpEndpointCheck>> endpointSupplier;

    public EndpointHealthIndicator(RestTemplate restTemplate, Supplier<List<HttpEndpointCheck>> endpointSupplier) {
        this.restTemplate = restTemplate;
        this.endpointSupplier = endpointSupplier;
    }

    @Override
    public Health health() {
        List<String> upEndpoints = new ArrayList<>();
        List<String> downEndpoints = new ArrayList<>();

        for (HttpEndpointCheck check : endpointSupplier.get()) {
            try {
                HttpMethod method;
                try {
                    method = HttpMethod.valueOf(check.getMethod().toUpperCase());
                } catch (IllegalArgumentException e) {
                    logger.warn("Unsupported HTTP method: {}", check.getMethod());
                    downEndpoints.add(check.getUri());
                    continue;
                }
                HttpHeaders headers = new HttpHeaders();
                if (check.getHeaders() != null) {
                    check.getHeaders().forEach(headers::set);
                }

                HttpEntity<?> entity = new HttpEntity<>(headers);
                ResponseEntity<Void> response = restTemplate.exchange(check.getUri(), method, entity, Void.class);

                if (response.getStatusCode().is2xxSuccessful()) {
                    upEndpoints.add(check.getUri());
                } else {
                    downEndpoints.add(check.getUri());
                }

            } catch (Exception e) {
                logger.error("Health check failed for {}: {}", check.getUri(), e.getMessage());
                downEndpoints.add(check.getUri());
            }
        }

        if (downEndpoints.isEmpty()) {
            return Health.up()
                    .withDetail("upEndpoints", upEndpoints)
                    .withDetail("downEndpoints", downEndpoints)
                    .build();
        } else {
            return Health.down()
                    .withDetail("upEndpoints", upEndpoints)
                    .withDetail("downEndpoints", downEndpoints)
                    .build();
        }
    }
}
