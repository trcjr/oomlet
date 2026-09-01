package com.github.trcjr.oomlet.controller;

import com.github.trcjr.oomlet.service.MemoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class MemoryControllerTest {
    private MockMvc mockMvc;

    @Mock
    private MemoryService memoryService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        MemoryController controller = new MemoryController(memoryService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

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
