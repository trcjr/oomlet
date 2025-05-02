package com.github.trcjr.oomlet.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.web.reactive.server.WebTestClient;

@WebFluxTest(StatusController.class)
class StatusControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void testSetStatus200WithZeroDelay() {
        webTestClient.get()
                .uri("/api/status?code=200&delayMillis=0")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .value(body -> body.contains("Returning HTTP status: 200 after 0 ms"));
    }

    @Test
    void testSetStatus404WithZeroDelay() {
        webTestClient.get()
                .uri("/api/status?code=404&delayMillis=0")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(String.class)
                .value(body -> body.contains("Returning HTTP status: 404 after 0 ms"));
    }

    @Test
    void testSetStatus500WithDelay() {
        webTestClient.get()
                .uri("/api/status?code=500&delayMillis=100")
                .exchange()
                .expectStatus().isEqualTo(500)
                .expectBody(String.class)
                .value(body -> body.contains("Returning HTTP status: 500 after 100 ms"));
    }

    @Test
    void testSetStatusHandlesErrorGracefully() {
        webTestClient.get()
                .uri("/api/status?code=500&delayMillis=-1")  // Negative delay triggers error
                .exchange()
                .expectStatus().isEqualTo(500)
                .expectBody(String.class)
                .value(body -> body.contains("Returning HTTP status: 500 due to interruption"));
    }
}