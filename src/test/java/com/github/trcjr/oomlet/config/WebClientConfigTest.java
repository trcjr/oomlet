package com.github.trcjr.oomlet.config;

import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

import static org.junit.jupiter.api.Assertions.*;

class WebClientConfigTest {

    @Test
    void webClientBuilder_returnsNonNullBuilder() {
        WebClientConfig config = new WebClientConfig();
        WebClient.Builder builder = config.webClientBuilder();
        assertNotNull(builder);

        // Optionally verify you can build a WebClient instance
        WebClient client = builder.build();
        assertNotNull(client);
    }
}