package com.github.trcjr.oomlet.config;

import com.github.trcjr.oomlet.HttpEndpointCheck;
import org.junit.jupiter.api.*;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;

class EndpointCheckConfigTest {

    private Path tempFile;

    @BeforeEach
    void setup() throws IOException {
        tempFile = Files.createTempFile("endpoint-health", ".yml");
    }

    @AfterEach
    void cleanup() throws IOException {
        if (Files.exists(tempFile)) {
            Files.delete(tempFile);
        }
    }

    @Test
    void testSupplierWhenFileDoesNotExistReturnsEmptyList() throws IOException {
        Files.delete(tempFile); // ensure it doesn't exist

        EndpointCheckConfig config = new EndpointCheckConfig(tempFile);
        Supplier<List<HttpEndpointCheck>> supplier = config.endpointCheckSupplier();

        List<HttpEndpointCheck> result = supplier.get();
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testSupplierWithValidYamlReturnsList() throws IOException {
        String yaml = "- uri: \"http://localhost\"\n  method: \"GET\"";
        Files.writeString(tempFile, yaml);

        EndpointCheckConfig config = new EndpointCheckConfig(tempFile);
        Supplier<List<HttpEndpointCheck>> supplier = config.endpointCheckSupplier();

        List<HttpEndpointCheck> result = supplier.get();
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("http://localhost", result.get(0).getUri());
        assertEquals("GET", result.get(0).getMethod());
    }

    @Test
    void testSupplierWithInvalidYamlReturnsEmptyList() throws IOException {
        Files.writeString(tempFile, "INVALID YAML");

        EndpointCheckConfig config = new EndpointCheckConfig(tempFile);
        Supplier<List<HttpEndpointCheck>> supplier = config.endpointCheckSupplier();

        List<HttpEndpointCheck> result = supplier.get();
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}