package com.github.trcjr.oomlet.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;

@WebMvcTest(RandomDataController.class)
public class RandomDataControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void random_validSize_returnsBytes() throws Exception {
        var mvcResult = mockMvc.perform(get("/api/random-data").param("size", "1024"))
            .andExpect(request().asyncStarted())
            .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(header().string("Content-Length", "1024"))
            .andExpect(result -> assertEquals(1024, result.getResponse().getContentAsByteArray().length));
    }

    @Test
    void random_zeroSize_returnsEmptyBody() throws Exception {
        var mvcResult = mockMvc.perform(get("/api/random-data").param("size", "0"))
            .andExpect(request().asyncStarted())
            .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
            .andExpect(status().isOk())
            .andExpect(header().string("Content-Length", "0"))
            .andExpect(result -> assertEquals(0, result.getResponse().getContentAsByteArray().length));
    }

    @Test
    void random_negativeSize_returnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/random-data").param("size", "-1"))
                .andExpect(status().isBadRequest());
    }
}
