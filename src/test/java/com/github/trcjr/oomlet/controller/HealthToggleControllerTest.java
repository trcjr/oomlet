package com.github.trcjr.oomlet.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.github.trcjr.oomlet.HealthToggleService;
import com.github.trcjr.oomlet.controller.HealthToggleController;

@WebMvcTest(controllers = HealthToggleController.class)
class HealthToggleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private HealthToggleService healthToggleService;

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
