package com.github.trcjr.oomlet.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/ping")
public class PingController {

    private final WebClient webClient = WebClient.builder().build();

    @GetMapping
    public Mono<ResponseEntity<String>> ping(@RequestParam String host) {
        return webClient
            .get()
            .uri(host)
            .retrieve()
            .toBodilessEntity()
            .map(response -> ResponseEntity
                .status(response.getStatusCode())
                .body("Pinged " + host + " - status: " + response.getStatusCodeValue())
            )
            .onErrorResume(e ->
                Mono.just(ResponseEntity
                    .status(500)
                    .body("Failed to ping " + host + " - error: " + e.getMessage()))
            );
    }
}