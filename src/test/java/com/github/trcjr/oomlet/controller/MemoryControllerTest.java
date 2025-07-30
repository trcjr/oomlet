package com.github.trcjr.oomlet.controller;

import com.github.trcjr.oomlet.service.MemoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MemoryController.class)
public class MemoryControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MemoryService memoryService;

    @Test
    void testAllocateMemorySuccess() throws Exception {
        Map<String, Object> mockResponse = new HashMap<>();
        mockResponse.put("requestedBytes", 1048576L);
        mockResponse.put("allocatedBytes", 1048576L);
        mockResponse.put("failedBytes", 0L);

        when(memoryService.allocateMemory(1048576)).thenReturn(mockResponse);

        mockMvc.perform(get("/api/allocate-memory")
                .param("bytes", "1048576")) // 1 MB
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.requestedBytes").value(1048576))
            .andExpect(jsonPath("$.allocatedBytes").value(1048576))
            .andExpect(jsonPath("$.failedBytes").value(0));
    }

    @Test
    void testAllocateMemoryZero() throws Exception {
        Map<String, Object> mockResponse = new HashMap<>();
        mockResponse.put("requestedBytes", 0L);
        mockResponse.put("allocatedBytes", 0L);
        mockResponse.put("failedBytes", 0L);

        when(memoryService.allocateMemory(0)).thenReturn(mockResponse);

        mockMvc.perform(get("/api/allocate-memory")
                .param("bytes", "0"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.requestedBytes").value(0))
            .andExpect(jsonPath("$.allocatedBytes").value(0))
            .andExpect(jsonPath("$.failedBytes").value(0));
    }

    @Test
    void testAllocateMemoryPartialFailure() throws Exception {
        Map<String, Object> mockResponse = new HashMap<>();
        mockResponse.put("requestedBytes", 1048576L);
        mockResponse.put("allocatedBytes", 524288L); // Only half allocated
        mockResponse.put("failedBytes", 524288L);

        when(memoryService.allocateMemory(1048576)).thenReturn(mockResponse);

        mockMvc.perform(get("/api/allocate-memory")
                .param("bytes", "1048576"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.requestedBytes").value(1048576))
            .andExpect(jsonPath("$.allocatedBytes").value(524288))
            .andExpect(jsonPath("$.failedBytes").value(524288));
    }
}
