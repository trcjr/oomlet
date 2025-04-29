package com.github.trcjr.oomlet;

import com.github.trcjr.oomlet.dto.CpuBurnResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CpuBurnController.class)
class CpuBurnControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final CpuBurnController controller = new CpuBurnController();

    // Unit tests
    @Test
    void testBurnCpuDefaultDirectCall() {
        ResponseEntity<CpuBurnResponse> response = controller.burnCpu(1000, 1);
        CpuBurnResponse body = response.getBody();



        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1000, body.getRequestedMillis());
        assertEquals(1, body.getRequestedThreads());
        assertEquals("completed", body.getStatus());
    }

    @Test
    void testBurnCpuCustomValues() {
        ResponseEntity<CpuBurnResponse> response = controller.burnCpu(500, 2);
        CpuBurnResponse body = response.getBody();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(500, body.getRequestedMillis());
        assertEquals(2, body.getRequestedThreads());
        assertEquals("completed", body.getStatus());
    }

    // Integration tests
    @Test
    void testBurnCpuDefault() throws Exception {
        mockMvc.perform(get("/api/burn-cpu"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.requestedMillis", is(1000)))
                .andExpect(jsonPath("$.requestedThreads", is(1)))
                .andExpect(jsonPath("$.status", is("completed")));
    }

    @Test
    void testBurnCpuCustomParams() throws Exception {
        mockMvc.perform(get("/api/burn-cpu?millis=500&threads=2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.requestedMillis", is(500)))
                .andExpect(jsonPath("$.requestedThreads", is(2)))
                .andExpect(jsonPath("$.status", is("completed")));
    }
}