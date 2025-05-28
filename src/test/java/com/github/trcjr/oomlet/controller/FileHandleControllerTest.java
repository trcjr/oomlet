package com.github.trcjr.oomlet.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import com.github.trcjr.oomlet.controller.FileHandleController;

@WebMvcTest(FileHandleController.class)
class FileHandleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testOpenFilesSuccess() throws Exception {
        mockMvc.perform(get("/api/open-files")
                .param("count", "5"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.requested").value(5))
            .andExpect(jsonPath("$.successfullyOpened").value(5))
            .andExpect(jsonPath("$.failed").value(0));
    }

    @Test
    void testOpenFilesZero() throws Exception {
        mockMvc.perform(get("/api/open-files")
                .param("count", "0"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.requested").value(0))
            .andExpect(jsonPath("$.successfullyOpened").value(0))
            .andExpect(jsonPath("$.failed").value(0));
    }
}
