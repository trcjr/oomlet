package com.github.trcjr.oomlet.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CrashController.class)
class CrashControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @SpyBean
    private CrashController controller;

    @BeforeEach
    void setup() {
        // Overriding shutdown behavior to prevent actual System.exit
        doNothing().when(controller).shutdownWithCode(1);
        doNothing().when(controller).shutdownWithCode(137);
    }

    @Test
    void crash_withValidCode_returnsConfirmation() throws Exception {
        mockMvc.perform(post("/api/crash?code=137")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Crashing with exit code: 137"));

        verify(controller, times(1)).shutdownWithCode(137);
    }

    @Test
    void crash_withoutCode_returnsDefault() throws Exception {
        mockMvc.perform(post("/api/crash")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Crashing with exit code: 1"));

        verify(controller, times(1)).shutdownWithCode(1);
    }

    @Test
    void crash_withInvalidCode_returnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/crash?code=-5")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid exit code."));
    }
}
