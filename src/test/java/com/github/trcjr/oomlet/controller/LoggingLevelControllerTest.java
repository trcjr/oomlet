package com.github.trcjr.oomlet.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.logging.LoggerConfiguration;
import org.springframework.boot.logging.LogLevel;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doReturn;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.http.MediaType;
import org.springframework.boot.logging.LoggingSystem;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class LoggingLevelControllerTest {

    private MockMvc mockMvc;

    @Mock
    private LoggingSystem loggingSystem;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        doReturn(new LoggerConfiguration("org.springframework", LogLevel.INFO, LogLevel.INFO))
            .when(loggingSystem).getLoggerConfiguration("org.springframework");

        LoggingLevelController controller = new LoggingLevelController(loggingSystem);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

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
