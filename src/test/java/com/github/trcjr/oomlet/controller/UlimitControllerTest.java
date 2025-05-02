package com.github.trcjr.oomlet.controller;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.io.*;
import java.util.function.Supplier;

import static org.mockito.Mockito.*;

@WebFluxTest(UlimitController.class)
class UlimitControllerTest {

    @MockBean
    Supplier<Process> mockProcessSupplier;

    @Test
    void testGetUlimitsReturnsSuccess() throws Exception {
        String ulimitOutput = "core file size          (blocks, -c) 0\n" +
                "data seg size           (kbytes, -d) unlimited\n";

        Process mockProcess = mock(Process.class);
        when(mockProcess.getInputStream()).thenReturn(new ByteArrayInputStream(ulimitOutput.getBytes()));
        when(mockProcess.waitFor()).thenReturn(0);
        when(mockProcessSupplier.get()).thenReturn(mockProcess);

        UlimitController controller = new UlimitController(mockProcessSupplier);
        WebTestClient client = WebTestClient.bindToController(controller).build();

        client.get().uri("/api/ulimits")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.core_file_size").isEqualTo("0")
                .jsonPath("$.data_seg_size").isEqualTo("unlimited");
    }

    @Test
    void testGetUlimitsReturnsFailureOnBadExitCode() throws Exception {
        Process mockProcess = mock(Process.class);
        when(mockProcess.getInputStream()).thenReturn(InputStream.nullInputStream());
        when(mockProcess.waitFor()).thenReturn(1);
        when(mockProcessSupplier.get()).thenReturn(mockProcess);

        UlimitController controller = new UlimitController(mockProcessSupplier);
        WebTestClient client = WebTestClient.bindToController(controller).build();

        client.get().uri("/api/ulimits")
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody()
                .jsonPath("$.error").isEqualTo("Failed to fetch ulimits.");
    }

    @Test
    void testGetUlimitsSkipsEmptyLinesAndInvalidParts() throws Exception {
        String ulimitOutput = "\ninvalid_line_without_split\nstack size              (kbytes, -s) 8192\n";

        Process mockProcess = mock(Process.class);
        when(mockProcess.getInputStream()).thenReturn(new ByteArrayInputStream(ulimitOutput.getBytes()));
        when(mockProcess.waitFor()).thenReturn(0);
        when(mockProcessSupplier.get()).thenReturn(mockProcess);

        UlimitController controller = new UlimitController(mockProcessSupplier);
        WebTestClient client = WebTestClient.bindToController(controller).build();

        client.get().uri("/api/ulimits")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.stack_size").isEqualTo("8192");
    }

    @Test
    void testGetUlimitsReturnsFailureOnException() {
        when(mockProcessSupplier.get()).thenThrow(new RuntimeException("Simulated failure"));

        UlimitController controller = new UlimitController(mockProcessSupplier);
        WebTestClient client = WebTestClient.bindToController(controller).build();

        client.get().uri("/api/ulimits")
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody()
                .jsonPath("$.error").value(msg -> msg.toString().contains("Simulated failure"));
    }

    @Test
    void testDefaultConstructorExecutionPath() {
        UlimitController controller = new UlimitController();
        WebTestClient client = WebTestClient.bindToController(controller).build();

        client.get().uri("/api/ulimits")
                .exchange()
                .expectStatus().is2xxSuccessful();
    }

    @Test
    void testDefaultConstructorThrowsOnProcessStartFailure() {
        UlimitController controller = new UlimitController(() -> {
            throw new RuntimeException("Process start failure");
        });

        WebTestClient client = WebTestClient.bindToController(controller).build();

        client.get().uri("/api/ulimits")
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody()
                .jsonPath("$.error").value(msg -> msg.toString().contains("Process start failure"));
    }
}