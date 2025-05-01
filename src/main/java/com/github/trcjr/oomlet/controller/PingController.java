package com.github.trcjr.oomlet.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/ping")
public class PingController {

    private final WebClient webClient;

    public PingController(WebClient.Builder builder) {
        this.webClient = builder.build();
    }

    @GetMapping
    public Mono<ResponseEntity<String>> ping(@RequestParam String host) {
        return webClient
                .get()
                .uri(host)
                .retrieve()
                .toBodilessEntity()
                .map(response -> ResponseEntity.ok("Ping to " + host + " responded with status code: " + response.getStatusCode().value()))
                .onErrorResume(ex -> Mono.just(ResponseEntity.status(500).body("Ping failed: " + ex.getMessage())));
    }
}