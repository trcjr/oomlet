package com.github.trcjr.oomlet;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(MemoryController.class)
public class MemoryControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void testAllocateMemorySuccess() throws Exception {
        mockMvc.perform(get("/api/allocate-memory")
                .param("bytes", "1048576")) // 1 MB
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.requestedBytes").value(1048576))
            .andExpect(jsonPath("$.allocatedBytes").value(1048576))
            .andExpect(jsonPath("$.failedBytes").value(0));
    }

    @Test
    void testAllocateMemoryZero() throws Exception {
        mockMvc.perform(get("/api/allocate-memory")
                .param("bytes", "0"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.requestedBytes").value(0))
            .andExpect(jsonPath("$.allocatedBytes").value(0))
            .andExpect(jsonPath("$.failedBytes").value(0));
    }
}
