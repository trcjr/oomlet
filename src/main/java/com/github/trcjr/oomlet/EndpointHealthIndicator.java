package com.github.trcjr.oomlet;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.http.HttpMethod;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class EndpointHealthIndicator implements HealthIndicator {

    private final WebClient webClient;
    private final Supplier<List<HttpEndpointCheck>> endpointSupplier;

    public EndpointHealthIndicator(WebClient webClient, Supplier<List<HttpEndpointCheck>> endpointSupplier) {
        this.webClient = webClient;
        this.endpointSupplier = endpointSupplier;
    }

    @Override
    public Health health() {
        List<HttpEndpointCheck> endpointsToCheck = endpointSupplier.get();

        List<String> upEndpoints = new ArrayList<>();
        List<String> downEndpoints = new ArrayList<>();

        for (HttpEndpointCheck check : endpointsToCheck) {
            try {
                boolean success = performCheck(check);
                if (success) {
                    upEndpoints.add(check.getUri());
                } else {
                    downEndpoints.add(check.getUri());
                }
            } catch (Exception e) {
                downEndpoints.add(check.getUri());
            }
        }

        return (downEndpoints.isEmpty() ? Health.up() : Health.down())
                .withDetail("upEndpoints", upEndpoints)
                .withDetail("downEndpoints", downEndpoints)
                .build();
    }

    boolean performCheck(HttpEndpointCheck check) {
        try {
            WebClient.RequestHeadersSpec<?> requestSpec = webClient.method(HttpMethod.valueOf(check.getMethod().toUpperCase()))
                    .uri(check.getUri())
                    .headers(headers -> {
                        if (check.getHeaders() != null) {
                            check.getHeaders().forEach(headers::add);
                        }
                    });

            Mono<Integer> responseMono = requestSpec
                    .retrieve()
                    .toBodilessEntity()
                    .timeout(Duration.ofSeconds(3))
                    .map(response -> response.getStatusCode().value());

            Integer statusCode = responseMono.block();

            return statusCode != null && statusCode >= 200 && statusCode < 300;
        } catch (Exception e) {
            return false;
        }
    }
}