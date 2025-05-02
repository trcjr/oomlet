package com.github.trcjr.oomlet.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.time.Duration;

@RestController
@RequestMapping("/api/status")
public class StatusController {

    @GetMapping
    public Mono<ResponseEntity<String>> setStatus(
            @RequestParam(defaultValue = "200") int code,
            @RequestParam(defaultValue = "0") long delayMillis) {

        return Mono.delay(Duration.ofMillis(delayMillis))
                .map(ignored -> {
                    String body = "Returning HTTP status: " + code + " after " + delayMillis + " ms";
                    return ResponseEntity.status(code).body(body);
                })
                .onErrorResume(ex -> {
                    String body = "Returning HTTP status: 500 due to interruption";
                    return Mono.just(ResponseEntity.status(500).body(body));
                });
    }
}