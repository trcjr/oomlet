package com.github.trcjr.oomlet;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.actuate.health.Health;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.*;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class EndpointHealthIndicatorTest {

    private WebClient mockWebClient;
    private RequestBodyUriSpec mockRequestBodyUriSpec;
    private ResponseSpec mockResponseSpec;

    @BeforeEach
    void setUp() {
        mockWebClient = mock(WebClient.class);
        mockRequestBodyUriSpec = mock(RequestBodyUriSpec.class);
        mockResponseSpec = mock(ResponseSpec.class);
    }

    @Test
    void health_whenAllEndpointsAreUp_returnsUp() {
        HttpEndpointCheck check = new HttpEndpointCheck("http://up.example.com", "GET", null, null);
        Supplier<List<HttpEndpointCheck>> supplier = () -> List.of(check);

        when(mockWebClient.method(HttpMethod.GET)).thenReturn(mockRequestBodyUriSpec);
        when(mockRequestBodyUriSpec.uri(check.getUri())).thenReturn(mockRequestBodyUriSpec);
        when(mockRequestBodyUriSpec.headers(any())).thenReturn(mockRequestBodyUriSpec);
        when(mockRequestBodyUriSpec.retrieve()).thenReturn(mockResponseSpec);
        when(mockResponseSpec.toBodilessEntity()).thenReturn(Mono.just(ResponseEntity.ok().build()));

        EndpointHealthIndicator indicator = new EndpointHealthIndicator(mockWebClient, supplier);
        Health health = indicator.health();

        assertEquals("UP", health.getStatus().getCode());
        assertTrue(((List<?>) health.getDetails().get("upEndpoints")).contains("http://up.example.com"));
        assertTrue(((List<?>) health.getDetails().get("downEndpoints")).isEmpty());
    }

    @Test
    void health_whenSomeEndpointsAreDown_returnsDown() {
        HttpEndpointCheck up = new HttpEndpointCheck("http://up.example.com", "GET", null, null);
        HttpEndpointCheck down = new HttpEndpointCheck("http://down.example.com", "GET", null, null);
        Supplier<List<HttpEndpointCheck>> supplier = () -> List.of(up, down);

        when(mockWebClient.method(HttpMethod.GET)).thenReturn(mockRequestBodyUriSpec);
        when(mockRequestBodyUriSpec.uri(up.getUri())).thenReturn(mockRequestBodyUriSpec);
        when(mockRequestBodyUriSpec.headers(any())).thenReturn(mockRequestBodyUriSpec);
        when(mockRequestBodyUriSpec.retrieve()).thenReturn(mockResponseSpec);
        when(mockResponseSpec.toBodilessEntity()).thenReturn(Mono.just(ResponseEntity.ok().build()))
                                                   .thenReturn(Mono.just(ResponseEntity.status(500).build()));

        EndpointHealthIndicator indicator = new EndpointHealthIndicator(mockWebClient, supplier);
        Health health = indicator.health();

        assertEquals("DOWN", health.getStatus().getCode());
        assertTrue(((List<?>) health.getDetails().get("upEndpoints")).contains("http://up.example.com"));
        assertTrue(((List<?>) health.getDetails().get("downEndpoints")).contains("http://down.example.com"));
    }

    @Test
    void health_whenAllEndpointsThrow_returnsDown() {
        HttpEndpointCheck check = new HttpEndpointCheck("http://fail.example.com", "GET", null, null);
        Supplier<List<HttpEndpointCheck>> supplier = () -> List.of(check);

        when(mockWebClient.method(HttpMethod.GET)).thenReturn(mockRequestBodyUriSpec);
        when(mockRequestBodyUriSpec.uri(anyString())).thenReturn(mockRequestBodyUriSpec);
        when(mockRequestBodyUriSpec.headers(any())).thenReturn(mockRequestBodyUriSpec);
        when(mockRequestBodyUriSpec.retrieve()).thenThrow(new RuntimeException("Simulated failure"));

        EndpointHealthIndicator indicator = new EndpointHealthIndicator(mockWebClient, supplier);
        Health health = indicator.health();

        assertEquals("DOWN", health.getStatus().getCode());
        assertTrue(((List<?>) health.getDetails().get("downEndpoints")).contains("http://fail.example.com"));
    }

    @Test
    void health_whenNoEndpoints_returnsUpWithEmptyLists() {
        Supplier<List<HttpEndpointCheck>> supplier = List::of;
        EndpointHealthIndicator indicator = new EndpointHealthIndicator(mockWebClient, supplier);
        Health health = indicator.health();

        assertEquals("UP", health.getStatus().getCode());
        assertTrue(((List<?>) health.getDetails().get("upEndpoints")).isEmpty());
        assertTrue(((List<?>) health.getDetails().get("downEndpoints")).isEmpty());
    }
}