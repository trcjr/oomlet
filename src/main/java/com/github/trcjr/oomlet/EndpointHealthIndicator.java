package com.github.trcjr.oomlet;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.util.function.Supplier;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Component
public class EndpointHealthIndicator implements HealthIndicator {

    protected WebClient webClient;
    protected List<HttpEndpointCheck> endpointsToCheck;

    public EndpointHealthIndicator(WebClient webClient, Supplier<List<HttpEndpointCheck>> endpointSupplier) {
        this.webClient = webClient;
        this.endpointsToCheck = endpointSupplier.get();
    }

    protected List<HttpEndpointCheck> loadEndpointsFromYaml() {
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.dataformat.yaml.YAMLMapper();
            java.nio.file.Path path = java.nio.file.Paths.get("/opt/endpoint_health_indicator_config.yml");
            if (!java.nio.file.Files.exists(path)) {
                return List.of();
            }
            java.util.List<HttpEndpointCheck> endpoints = java.util.Arrays.asList(
                    mapper.readValue(java.nio.file.Files.newInputStream(path), HttpEndpointCheck[].class)
            );
            return endpoints;
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public Health health() {
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

    protected boolean performCheck(HttpEndpointCheck check) {
        try {
            WebClient.RequestBodySpec requestSpec = webClient.method(HttpMethod.valueOf(check.getMethod().toUpperCase()))
                    .uri(check.getUri())
                    .headers(httpHeaders -> {
                        if (check.getHeaders() != null) {
                            check.getHeaders().forEach(httpHeaders::add);
                        }
                    });

            Mono<Integer> responseMono = requestSpec
                    .retrieve()
                    .toBodilessEntity()
                    .timeout(Duration.ofSeconds(3))
                    .map(responseEntity -> responseEntity.getStatusCode().value());

            Integer statusCode = responseMono.block();

            return statusCode != null && statusCode >= 200 && statusCode < 300;
        } catch (Exception e) {
            return false;
        }
    }
}