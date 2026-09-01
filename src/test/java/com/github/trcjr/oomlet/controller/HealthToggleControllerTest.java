package com.github.trcjr.oomlet.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.github.trcjr.oomlet.HealthToggleService;

class HealthToggleControllerTest {

    private MockMvc mockMvc;

    @Mock
    private HealthToggleService healthToggleService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        HealthToggleController controller = new HealthToggleController(healthToggleService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void testEnableHealth() throws Exception {
        mockMvc.perform(post("/api/health-toggle/enable"))
                .andExpect(status().isOk());
    }

    @Test
    void testDisableHealth() throws Exception {
        mockMvc.perform(post("/api/health-toggle/disable"))
                .andExpect(status().isOk());
    }
}
