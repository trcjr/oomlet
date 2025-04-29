package com.github.trcjr.oomlet.controller;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import com.github.trcjr.oomlet.controller.LoggingLevelController;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = com.github.trcjr.oomlet.OomletApplication.class)
@AutoConfigureMockMvc
public class LoggingLevelControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testGetSpringLoggingLevel() throws Exception {
        mockMvc.perform(get("/api/logging/spring"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.['org.springframework']").exists());
    }

    @Test
    void testSetSpringLoggingLevelValid() throws Exception {
        mockMvc.perform(post("/api/logging/spring")
                .param("level", "DEBUG"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("DEBUG")));

        Logger logger = (Logger) LoggerFactory.getLogger("org.springframework");
        assert logger.getLevel() == Level.DEBUG;
    }

    @Test
    void testSetSpringLoggingLevelInvalid() throws Exception {
        mockMvc.perform(post("/api/logging/spring")
                .param("level", "NOPE"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Invalid log level")));
    }
}
