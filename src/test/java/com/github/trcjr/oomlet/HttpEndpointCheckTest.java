package com.github.trcjr.oomlet;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class HttpEndpointCheckTest {

    @Nested
    class CoreBehavior {

        @Test
        void gettersAndSetters_shouldBehaveCorrectly() {
            HttpEndpointCheck check = new HttpEndpointCheck();
            check.setUri("http://localhost");
            check.setMethod("GET");
            check.setHeaders(Map.of("Authorization", "Bearer xyz"));
            check.setPayload("{ \"data\": 123 }");

            assertEquals("http://localhost", check.getUri());
            assertEquals("GET", check.getMethod());
            assertEquals("Bearer xyz", check.getHeaders().get("Authorization"));
            assertEquals("{ \"data\": 123 }", check.getPayload());
        }

        @Test
        void allArgsConstructor_shouldSetFields() {
            HttpEndpointCheck check = new HttpEndpointCheck(
                "http://localhost", "POST", Map.of("X-Test", "yes"), "payload");

            assertEquals("http://localhost", check.getUri());
            assertEquals("POST", check.getMethod());
            assertEquals("yes", check.getHeaders().get("X-Test"));
            assertEquals("payload", check.getPayload());
        }

        @Test
        void equals_shouldWorkAsExpected() {
            HttpEndpointCheck check1 = new HttpEndpointCheck(
                "http://localhost", "GET", Map.of("X", "1"), "p");

            HttpEndpointCheck check2 = new HttpEndpointCheck(
                "http://localhost", "GET", Map.of("X", "1"), "p");

            HttpEndpointCheck check3 = new HttpEndpointCheck(
                "http://other", "POST", Map.of(), null);

            assertEquals(check1, check2);
            assertNotEquals(check1, check3);
            assertNotEquals(check1, null);
            assertNotEquals(check1, "not a check");
        }

        @Test
        void hashCode_shouldBeConsistentWithEquals() {
            HttpEndpointCheck check1 = new HttpEndpointCheck(
                "http://localhost", "GET", Map.of("X", "1"), "p");

            HttpEndpointCheck check2 = new HttpEndpointCheck(
                "http://localhost", "GET", Map.of("X", "1"), "p");

            assertEquals(check1.hashCode(), check2.hashCode());
        }

        @Test
        void toString_shouldIncludeKeyFields() {
            HttpEndpointCheck check = new HttpEndpointCheck(
                "http://localhost", "GET", Map.of("Auth", "yes"), "data");

            String str = check.toString();
            assertTrue(str.contains("localhost"));
            assertTrue(str.contains("GET"));
            assertTrue(str.contains("Auth"));
        }
    }

    @Nested
    class EdgeCases {

        @Test
        void equals_shouldHandleNullFieldsSafely() {
            HttpEndpointCheck check1 = new HttpEndpointCheck();
            HttpEndpointCheck check2 = new HttpEndpointCheck();

            assertEquals(check1, check2);
            assertEquals(check1.hashCode(), check2.hashCode());
        }

        @Test
        void equals_shouldHandleMixedNullAndNonNullFields() {
            HttpEndpointCheck check1 = new HttpEndpointCheck("http://a", null, null, null);
            HttpEndpointCheck check2 = new HttpEndpointCheck("http://a", null, null, null);

            assertEquals(check1, check2);
        }

        @Test
        void equals_shouldReturnFalseForDifferingNulls() {
            HttpEndpointCheck a = new HttpEndpointCheck("http://a", null, null, null);
            HttpEndpointCheck b = new HttpEndpointCheck("http://a", "GET", null, null);

            assertNotEquals(a, b);
        }

        @Test
        void toString_shouldHandleNullsGracefully() {
            HttpEndpointCheck check = new HttpEndpointCheck(null, null, null, null);
            String output = check.toString();

            assertNotNull(output);
            assertTrue(output.contains("null"));
        }

        @Test
        void hashCode_shouldNotThrowWithNullFields() {
            HttpEndpointCheck check = new HttpEndpointCheck(null, null, null, null);
            assertDoesNotThrow(check::hashCode);
        }

        @Test
        void setters_shouldAcceptNulls() {
            HttpEndpointCheck check = new HttpEndpointCheck();
            assertDoesNotThrow(() -> {
                check.setUri(null);
                check.setMethod(null);
                check.setHeaders(null);
                check.setPayload(null);
            });
        }

        @Test
        void equals_shouldRejectDifferentObjectTypes() {
            HttpEndpointCheck check = new HttpEndpointCheck("a", "GET", Map.of(), "x");
            assertNotEquals(check, "not-an-endpoint");
        }
    }
}
