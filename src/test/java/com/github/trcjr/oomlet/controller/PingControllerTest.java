package com.github.trcjr.oomlet.controller;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.client.WebClient;

import static org.hamcrest.Matchers.containsString;

@WebFluxTest(PingController.class)
@Import({PingController.class, PingControllerTest.TestConfig.class})
class PingControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    private static MockWebServer mockWebServer;

    @BeforeAll
    static void setUp() throws Exception {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
    }

    @AfterAll
    static void tearDown() throws Exception {
        mockWebServer.shutdown();
    }

    @TestConfiguration
    static class TestConfig {
        @Bean
        public WebClient.Builder webClientBuilder() {
            return WebClient.builder().baseUrl(mockWebServer.url("/").toString());
        }
    }

    @Test
    void ping_validHost_returnsStatus() {
        mockWebServer.enqueue(new MockResponse().setResponseCode(200));

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/ping")
                        .queryParam("host", "/test")
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .value(containsString("Ping to /test responded with status code: 200"));
    }

    @Test
    void ping_invalidHost_returnsError() {
        webTestClient.get()
                .uri("/api/ping?host=http://localhost:9999")
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody(String.class)
                .value(containsString("Ping failed"));
    }
}