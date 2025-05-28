package com.github.trcjr.oomlet.controller;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = StatusController.class)
class StatusControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testSetStatus200WithZeroDelay() throws Exception {
        mockMvc.perform(get("/api/status")
                .param("code", "200")
                .param("delayMillis", "0"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Returning HTTP status: 200")));
    }

    @Test
    void testSetStatus404WithZeroDelay() throws Exception {
        mockMvc.perform(get("/api/status")
                .param("code", "404")
                .param("delayMillis", "0"))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Returning HTTP status: 404")));
    }

    @Test
    void testSetStatus500WithDelay() throws Exception {
        mockMvc.perform(get("/api/status")
                .param("code", "500")
                .param("delayMillis", "10"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(containsString("Returning HTTP status: 500")));
    }

    @Test
    void testSetStatusHandlesTooLargeDelay() throws Exception {
        mockMvc.perform(get("/api/status")
                .param("code", "200")
                .param("delayMillis", "999999999"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Invalid delay")));
    }

    @Test
    void testSetStatusWithInvalidStatusCode() throws Exception {
        mockMvc.perform(get("/api/status")
                .param("code", "999")
                .param("delayMillis", "0"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Invalid status code")));
    }
}
