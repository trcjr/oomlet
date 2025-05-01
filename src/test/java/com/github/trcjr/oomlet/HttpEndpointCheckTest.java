package com.github.trcjr.oomlet;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class HttpEndpointCheckTest {

    @Test
    void testAllArgsConstructorAndGetters() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer token");
        String payload = "{\"key\":\"value\"}";

        HttpEndpointCheck check = new HttpEndpointCheck("http://localhost:8080", "GET", headers, payload);

        assertEquals("http://localhost:8080", check.getUri());
        assertEquals("GET", check.getMethod());
        assertEquals(headers, check.getHeaders());
        assertEquals(payload, check.getPayload());
    }

    @Test
    void testNoArgsConstructorAndSetters() {
        HttpEndpointCheck check = new HttpEndpointCheck();

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");

        check.setUri("http://test.com");
        check.setMethod("POST");
        check.setHeaders(headers);
        check.setPayload("some payload");

        assertEquals("http://test.com", check.getUri());
        assertEquals("POST", check.getMethod());
        assertEquals(headers, check.getHeaders());
        assertEquals("some payload", check.getPayload());
    }

    @Test
    void testNullHeadersAndPayloadDefaults() {
        HttpEndpointCheck check = new HttpEndpointCheck();
        assertNull(check.getHeaders());
        assertNull(check.getPayload());
    }
}