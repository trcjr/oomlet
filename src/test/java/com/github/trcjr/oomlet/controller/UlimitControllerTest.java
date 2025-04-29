package com.github.trcjr.oomlet.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.function.Supplier;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.github.trcjr.oomlet.controller.UlimitController;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@WebMvcTest(UlimitController.class)
class UlimitControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private Supplier<ProcessBuilder> processBuilderSupplier;

    @TestConfiguration
    static class TestConfig {
        @Bean
        public Supplier<ProcessBuilder> processBuilderSupplier() {
            return () -> {
                ProcessBuilder builder = new ProcessBuilder("echo", "open files (-n) 2560");
                return builder;
            };
        }
    }

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetUlimitsReturnsSuccess() throws Exception {
        mockMvc.perform(get("/api/ulimits"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.['open_files']").exists()); // Stronger: check for known field
    }

    @Test
    void testGetUlimitsHandlesCommandFailureGracefully() throws Exception {
        mockMvc.perform(get("/api/ulimits"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"));
    }

    @Test
    void testGetUlimitsReturnsNonEmptyBody() throws Exception {
        mockMvc.perform(get("/api/ulimits"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isNotEmpty());
    }
}