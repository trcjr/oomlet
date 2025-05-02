package com.github.trcjr.oomlet;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.actuate.health.Health;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class EndpointHealthIndicatorTest {

    private RestTemplate restTemplate;
    private EndpointHealthIndicator indicator;

    @BeforeEach
    void setup() {
        restTemplate = mock(RestTemplate.class);

        HttpEndpointCheck check = new HttpEndpointCheck("http://test.com", "GET", Map.of("Authorization", "Bearer token"), null);
        Supplier<List<HttpEndpointCheck>> endpointSupplier = () -> List.of(check);

        ResponseEntity<Void> response = ResponseEntity.ok().build();
        when(restTemplate.exchange(eq("http://test.com"), eq(HttpMethod.GET), any(HttpEntity.class), eq(Void.class)))
                .thenReturn(response);

        indicator = new EndpointHealthIndicator(restTemplate, endpointSupplier);
    }

    @Test
    void health_withAllUp_shouldBeUp() {
        Health result = indicator.health();
        assertEquals("UP", result.getStatus().getCode());
        assertTrue(((List<?>) result.getDetails().get("downEndpoints")).isEmpty());
    }

    @Test
    void health_withAllDown_shouldBeDown() {
        when(restTemplate.exchange(eq("http://fail.com"), eq(HttpMethod.GET), any(HttpEntity.class), eq(Void.class)))
                .thenThrow(new RuntimeException("fail"));

        HttpEndpointCheck check = new HttpEndpointCheck("http://fail.com", "GET", null, null);
        Supplier<List<HttpEndpointCheck>> endpointSupplier = () -> List.of(check);
        indicator = new EndpointHealthIndicator(restTemplate, endpointSupplier);

        Health result = indicator.health();
        assertEquals("DOWN", result.getStatus().getCode());
        List<?> down = (List<?>) result.getDetails().get("downEndpoints");
        assertEquals(1, down.size());
        assertEquals("http://fail.com", down.get(0));
    }

    @Test
    void health_withNoEndpoints_shouldBeUpWithEmptyLists() {
        Supplier<List<HttpEndpointCheck>> emptySupplier = List::of;
        indicator = new EndpointHealthIndicator(restTemplate, emptySupplier);

        Health result = indicator.health();
        assertEquals("UP", result.getStatus().getCode());
        assertEquals(List.of(), result.getDetails().get("upEndpoints"));
        assertEquals(List.of(), result.getDetails().get("downEndpoints"));
    }

    @Test
    void performCheck_shouldSetHeaders() {
        HttpEndpointCheck check = new HttpEndpointCheck("http://test.com", "GET", Map.of("X-Test", "123"), null);
        Supplier<List<HttpEndpointCheck>> supplier = () -> List.of(check);
        indicator = new EndpointHealthIndicator(restTemplate, supplier);

        indicator.health();

        // Verify that the correct exchange method was called with headers
        verify(restTemplate).exchange(eq("http://test.com"), eq(HttpMethod.GET), argThat(entity -> {
            HttpHeaders headers = entity.getHeaders();
            return "123".equals(headers.getFirst("X-Test"));
        }), eq(Void.class));
    }
}