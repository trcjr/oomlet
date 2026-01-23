package com.github.trcjr.oomlet.controller;

import com.github.trcjr.oomlet.service.FileHandleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class FileHandleControllerTest {

    private MockMvc mockMvc;

    @Mock
    private FileHandleService fileHandleService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        FileHandleController controller = new FileHandleController(fileHandleService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void testOpenFilesSuccess() throws Exception {
        Map<String, Object> mockResponse = new HashMap<>();
        mockResponse.put("requested", 5);
        mockResponse.put("successfullyOpened", 5);
        mockResponse.put("failed", 0);

        when(fileHandleService.openFiles(5)).thenReturn(mockResponse);

        mockMvc.perform(get("/api/open-files")
                .param("count", "5"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.requested").value(5))
            .andExpect(jsonPath("$.successfullyOpened").value(5))
            .andExpect(jsonPath("$.failed").value(0));
    }

    @Test
    void testOpenFilesZero() throws Exception {
        Map<String, Object> mockResponse = new HashMap<>();
        mockResponse.put("requested", 0);
        mockResponse.put("successfullyOpened", 0);
        mockResponse.put("failed", 0);

        when(fileHandleService.openFiles(0)).thenReturn(mockResponse);

        mockMvc.perform(get("/api/open-files")
                .param("count", "0"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.requested").value(0))
            .andExpect(jsonPath("$.successfullyOpened").value(0))
            .andExpect(jsonPath("$.failed").value(0));
    }

    @Test
    void testOpenFilesPartialFailure() throws Exception {
        Map<String, Object> mockResponse = new HashMap<>();
        mockResponse.put("requested", 10);
        mockResponse.put("successfullyOpened", 7);
        mockResponse.put("failed", 3);

        when(fileHandleService.openFiles(10)).thenReturn(mockResponse);

        mockMvc.perform(get("/api/open-files")
                .param("count", "10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.requested").value(10))
            .andExpect(jsonPath("$.successfullyOpened").value(7))
            .andExpect(jsonPath("$.failed").value(3));
    }
}
