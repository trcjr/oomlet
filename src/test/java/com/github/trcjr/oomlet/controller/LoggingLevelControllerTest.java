package com.github.trcjr.oomlet.controller;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = LoggingLevelController.class)
public class LoggingLevelControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testGetSpringLoggingLevel() throws Exception {
        mockMvc.perform(get("/api/logging/spring"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.['org.springframework']").exists());
    }

    @Test
    void testSetSpringLoggingLevelValid() throws Exception {
        mockMvc.perform(post("/api/logging/spring")
                        .param("level", "DEBUG"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Logging level for org.springframework set to DEBUG")));
    }

    @Test
    void testSetSpringLoggingLevelInvalid() throws Exception {
        mockMvc.perform(post("/api/logging/spring")
                        .param("level", "NOPE"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Invalid log level: NOPE")));
    }
}