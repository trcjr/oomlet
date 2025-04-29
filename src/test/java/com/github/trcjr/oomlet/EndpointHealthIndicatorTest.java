package com.github.trcjr.oomlet;

import org.junit.jupiter.api.Test;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class EndpointHealthIndicatorTest {

    @Autowired
    private MockMvc mockMvc;

    private final EndpointHealthIndicator indicator = new EndpointHealthIndicator();

    // Unit test
    @Test
    void testHealthWithUpAndDownEndpoints() {
        Health health = indicator.health();

        assertNotNull(health);
        assertTrue(health.getDetails().containsKey("upEndpoints"));
        assertTrue(health.getDetails().containsKey("downEndpoints"));

        var upEndpoints = (java.util.List<String>) health.getDetails().get("upEndpoints");
        var downEndpoints = (java.util.List<String>) health.getDetails().get("downEndpoints");

        assertNotNull(upEndpoints);
        assertNotNull(downEndpoints);
    }

    // Integration test
    @Test
    void testActuatorHealthEndpoint() throws Exception {
        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").exists());
    }
}