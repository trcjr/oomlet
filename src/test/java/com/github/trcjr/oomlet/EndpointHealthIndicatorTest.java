package com.github.trcjr.oomlet;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.actuate.health.Health;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.*;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EndpointHealthIndicatorTest {

    private WebClient webClient;
    private WebClient.RequestBodyUriSpec uriSpec;
    private WebClient.RequestHeadersSpec<?> headersSpec;
    private WebClient.ResponseSpec responseSpec;

    private EndpointHealthIndicator indicator;

    @BeforeEach
    void setup() {
        webClient = mock(WebClient.class);
        uriSpec = mock(WebClient.RequestBodyUriSpec.class);
        headersSpec = mock(WebClient.RequestHeadersSpec.class);
        responseSpec = mock(WebClient.ResponseSpec.class);

        when(webClient.method(any())).thenReturn(uriSpec);
        when(uriSpec.uri(anyString())).thenReturn(uriSpec);
        when(uriSpec.headers(any())).thenReturn(uriSpec);
        when(uriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.toBodilessEntity()).thenReturn(Mono.just(ResponseEntity.ok().build()));

        HttpEndpointCheck check = new HttpEndpointCheck("http://test.com", "GET", Map.of("Authorization", "Bearer token"), null);
        Supplier<List<HttpEndpointCheck>> endpointSupplier = () -> List.of(check);

        indicator = new EndpointHealthIndicator(webClient, endpointSupplier);
    }

    @Test
    void health_withAllUp_shouldBeUp() {
        Health result = indicator.health();
        assertEquals("UP", result.getStatus().getCode());
        assertTrue(((List<?>) result.getDetails().get("downEndpoints")).isEmpty());
    }

    @Test
    void health_withAllDown_shouldBeDown() {
        when(responseSpec.toBodilessEntity()).thenThrow(new RuntimeException("fail"));

        HttpEndpointCheck check = new HttpEndpointCheck("http://fail.com", "GET", null, null);
        Supplier<List<HttpEndpointCheck>> endpointSupplier = () -> List.of(check);
        indicator = new EndpointHealthIndicator(webClient, endpointSupplier);

        Health result = indicator.health();
        assertEquals("DOWN", result.getStatus().getCode());
        List<?> down = (List<?>) result.getDetails().get("downEndpoints");
        assertEquals(1, down.size());
        assertEquals("http://fail.com", down.get(0));
    }

    @Test
    void health_withNoEndpoints_shouldBeUpWithEmptyLists() {
        Supplier<List<HttpEndpointCheck>> emptySupplier = List::of;
        indicator = new EndpointHealthIndicator(webClient, emptySupplier);

        Health result = indicator.health();
        assertEquals("UP", result.getStatus().getCode());
        assertEquals(List.of(), result.getDetails().get("upEndpoints"));
        assertEquals(List.of(), result.getDetails().get("downEndpoints"));
    }

    @Test
    void performCheck_shouldSetHeaders() {
        HttpEndpointCheck check = new HttpEndpointCheck("http://test.com", "GET", Map.of("X-Test", "123"), null);
        Supplier<List<HttpEndpointCheck>> supplier = () -> List.of(check);
        indicator = new EndpointHealthIndicator(webClient, supplier);

        // Trigger health to indirectly test header injection
        indicator.health();

        verify(uriSpec).headers(any());
    }
}