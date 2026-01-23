package com.github.trcjr.oomlet.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class UlimitControllerTest {

    private MockMvc mockMvc;
    private static final String VALID_ULIMIT_OUTPUT =
        "core file size          (blocks, -c) 0\n" +
        "data seg size           (kbytes, -d) unlimited\n" +
        "file size               (blocks, -f) unlimited\n" +
        "max locked memory       (kbytes, -l) unlimited\n" +
        "max memory size         (kbytes, -m) unlimited\n" +
        "open files              (-n) 10240\n" +
        "pipe size               (512 bytes, -p) 1\n" +
        "stack size              (kbytes, -s) 8176\n" +
        "cpu time                (seconds, -t) unlimited\n" +
        "max user processes      (-u) 1333\n" +
        "virtual memory          (kbytes, -v) unlimited\n";

    private static String injectedOutput = VALID_ULIMIT_OUTPUT;

    @BeforeEach
    void setup() {
        UlimitController controller = new UlimitController(() -> {
            if ("__throw__".equals(injectedOutput)) {
                throw new RuntimeException("boom");
            }
            return new MockProcess(injectedOutput);
        });
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    static class MockProcess extends Process {
        private final InputStream stdout;

        MockProcess(String output) {
            this.stdout = new ByteArrayInputStream(output.getBytes(StandardCharsets.UTF_8));
        }

        @Override public InputStream getInputStream() { return stdout; }
        @Override public InputStream getErrorStream() { return InputStream.nullInputStream(); }
        @Override public OutputStream getOutputStream() { throw new UnsupportedOperationException(); }
        @Override public int waitFor() { return 0; }
        @Override public int exitValue() { return 0; }
        @Override public void destroy() {}
    }

    @Nested
    @DisplayName("Normal Behavior")
    class NormalBehavior {
        @Test
        void getLimits_shouldReturnValidJsonWithExpectedFields() throws Exception {
            injectedOutput = VALID_ULIMIT_OUTPUT;
            mockMvc.perform(get("/api/ulimits"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", aMapWithSize(11)))
                .andExpect(jsonPath("$.core_file_size", is("0")))
                .andExpect(jsonPath("$.open_files", is("10240")))
                .andExpect(jsonPath("$.max_user_processes", is("1333")));
        }
    }

    @Nested
    @DisplayName("Error Handling")
    class ErrorHandling {

        @Test
        void getLimits_shouldReturn500_onInvalidOutput() throws Exception {
            injectedOutput = "garbage output that makes no sense";

            mockMvc.perform(get("/api/ulimits"))
                .andExpect(status().isInternalServerError());

            injectedOutput = VALID_ULIMIT_OUTPUT;
        }

        @Test
        void getLimits_shouldReturn500_onProcessThrowsException() throws Exception {
            injectedOutput = "__throw__";

            mockMvc.perform(get("/api/ulimits"))
                .andExpect(status().isInternalServerError());

            injectedOutput = VALID_ULIMIT_OUTPUT;
        }
    }

    @Nested
    @DisplayName("Edge Cases")
    class EdgeCases {

        @Test
        void getLimits_shouldReturnEmptyJsonIfNothingParsed() throws Exception {
            injectedOutput = "";

            mockMvc.perform(get("/api/ulimits"))
                .andExpect(status().isInternalServerError());

            injectedOutput = VALID_ULIMIT_OUTPUT;
        }

        @Test
        void getLimits_shouldTrimAndNormalizeKeys() throws Exception {
            injectedOutput = "   open files   (-n)   123   \n";

            mockMvc.perform(get("/api/ulimits"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.open_files", is("123")));

            injectedOutput = VALID_ULIMIT_OUTPUT;
        }
    }
}
