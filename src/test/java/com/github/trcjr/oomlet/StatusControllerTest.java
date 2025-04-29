package com.github.trcjr.oomlet;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = StatusController.class)
class StatusControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testStatusDefault200() throws Exception {
        mockMvc.perform(get("/api/status"))
                .andExpect(status().isOk());
    }

    @Test
    void testStatusCustom() throws Exception {
        mockMvc.perform(get("/api/status?responseCode=404"))
                .andExpect(status().isNotFound());
    }
}   