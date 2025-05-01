package com.github.trcjr.oomlet.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LatencyController.class)
class LatencyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void simulateLatency_returnsResponseAfterDelay() throws Exception {
        long delay = 200;

        long start = System.currentTimeMillis();

        mockMvc.perform(get("/api/latency").param("delayMillis", String.valueOf(delay)))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Responded after " + delay + " ms")));

        long elapsed = System.currentTimeMillis() - start;
        assert elapsed >= delay : "Expected elapsed time to be at least " + delay + "ms";
    }

    @Test
    void simulateLatency_defaultsTo1000() throws Exception {
        mockMvc.perform(get("/api/latency"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Responded after 1000 ms")));
    }
}