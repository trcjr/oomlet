package com.github.trcjr.oomlet.controller;

import com.github.trcjr.oomlet.service.StatusService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = StatusController.class)
class StatusControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StatusService statusService;

    @Test
    void testSetStatus200WithZeroDelay() throws Exception {
        when(statusService.setStatus(200, 0)).thenReturn("Returning HTTP status: 200 after 0 ms");

        mockMvc.perform(get("/api/status")
                .param("code", "200")
                .param("delayMillis", "0"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Returning HTTP status: 200")));
    }

    @Test
    void testSetStatus404WithZeroDelay() throws Exception {
        when(statusService.setStatus(404, 0)).thenReturn("Returning HTTP status: 404 after 0 ms");

        mockMvc.perform(get("/api/status")
                .param("code", "404")
                .param("delayMillis", "0"))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Returning HTTP status: 404")));
    }

    @Test
    void testSetStatus500WithDelay() throws Exception {
        when(statusService.setStatus(500, 10)).thenReturn("Returning HTTP status: 500 after 10 ms");

        mockMvc.perform(get("/api/status")
                .param("code", "500")
                .param("delayMillis", "10"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(containsString("Returning HTTP status: 500")));
    }

    @Test
    void testSetStatusHandlesTooLargeDelay() throws Exception {
        when(statusService.setStatus(200, 999999999))
                .thenThrow(new IllegalArgumentException("Invalid delay. Max allowed is 30000 ms."));

        mockMvc.perform(get("/api/status")
                .param("code", "200")
                .param("delayMillis", "999999999"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Invalid delay")));
    }

    @Test
    void testSetStatusWithInvalidStatusCode() throws Exception {
        when(statusService.setStatus(999, 0))
                .thenThrow(new IllegalArgumentException("Invalid status code. Must be between 100 and 599."));

        mockMvc.perform(get("/api/status")
                .param("code", "999")
                .param("delayMillis", "0"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Invalid status code")));
    }

    @Test
    void testSetStatusHandlesInterruption() throws Exception {
        when(statusService.setStatus(200, 100))
                .thenThrow(new RuntimeException("Interrupted during delay"));

        mockMvc.perform(get("/api/status")
                .param("code", "200")
                .param("delayMillis", "100"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(containsString("Returning HTTP status: 500 due to interruption")));
    }

    @Test
    void testSetStatusHandlesOtherRuntimeException() throws Exception {
        when(statusService.setStatus(200, 100))
                .thenThrow(new RuntimeException("Some other runtime error"));

        // The controller re-throws RuntimeException, so Spring's error handling will return 500
        mockMvc.perform(get("/api/status")
                .param("code", "200")
                .param("delayMillis", "100"))
                .andExpect(status().isInternalServerError());
    }
}
