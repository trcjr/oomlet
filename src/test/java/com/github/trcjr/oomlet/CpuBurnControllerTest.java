package com.github.trcjr.oomlet;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.hamcrest.Matchers.is;

@WebMvcTest(CpuBurnController.class)
class CpuBurnControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testBurnCpuDefault() throws Exception {
        mockMvc.perform(get("/api/burn-cpu"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.requestedMillis", is(1000)))
                .andExpect(jsonPath("$.requestedThreads", is(1)))
                .andExpect(jsonPath("$.status", is("completed")));
    }

    @Test
    void testBurnCpuCustomParams() throws Exception {
        mockMvc.perform(get("/api/burn-cpu?millis=500&threads=2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.requestedMillis", is(500)))
                .andExpect(jsonPath("$.requestedThreads", is(2)))
                .andExpect(jsonPath("$.status", is("completed")));
    }
}