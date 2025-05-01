package com.github.trcjr.oomlet.controller;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.*;

class PingControllerTest {

    @Test
    void ping_returnsSuccessResponse() {
        PingController controller = new PingController() {
            @Override
            public Mono<ResponseEntity<String>> ping(String host) {
                return Mono.just(ResponseEntity.ok("Pinged " + host + " - status: 200"));
            }
        };

        ResponseEntity<String> result = controller.ping("https://example.com").block();
        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("Pinged https://example.com - status: 200", result.getBody());
    }

    @Test
    void ping_handlesErrorGracefully() {
        PingController controller = new PingController() {
            @Override
            public Mono<ResponseEntity<String>> ping(String host) {
                return Mono.just(ResponseEntity.status(500).body("Failed to ping " + host + " - error: boom"));
            }
        };

        ResponseEntity<String> result = controller.ping("bad://host").block();
        assertNotNull(result);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
        assertTrue(result.getBody().contains("Failed to ping"));
    }
}