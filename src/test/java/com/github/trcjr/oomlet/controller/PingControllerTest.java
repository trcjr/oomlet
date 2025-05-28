package com.github.trcjr.oomlet.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PingController.class)
class PingControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private RestTemplate restTemplate;

        @Nested
        @DisplayName("Normal Behavior")
        class NormalBehavior {
                @Test
                void ping_validHost_returnsStatus() throws Exception {
                        when(restTemplate.exchange(eq("http://localhost/test"), eq(HttpMethod.GET), any(),
                                        eq(Void.class)))
                                        .thenReturn(new ResponseEntity<>(HttpStatus.OK));

                        mockMvc.perform(get("/api/ping")
                                        .param("url", "http://localhost/test")
                                        .header("X-Test-Header", "ok"))
                                        .andExpect(status().isOk())
                                        .andExpect(content()
                                                        .string(containsString("Ping successful with status: 200")));
                }
        }

        @Nested
        @DisplayName("Error Handling")
        class ErrorHandling {
                @Test
                void ping_invalidHost_returnsError() throws Exception {
                        when(restTemplate.exchange(any(), eq(HttpMethod.GET), any(), eq(Void.class)))
                                        .thenThrow(new RuntimeException("connection refused"));

                        mockMvc.perform(get("/api/ping")
                                        .param("url", "http://badhost")
                                        .header("X-Test-Header", "ok"))
                                        .andExpect(status().isBadGateway())
                                        .andExpect(content().string(containsString("Ping failed")));
                }

                @Test
                void ping_missingUrlParam_returns400() throws Exception {
                        mockMvc.perform(get("/api/ping")
                                        .header("X-Test-Header", "ok"))
                                        .andExpect(status().isBadRequest());
                }
        }

        @Nested
        @DisplayName("Edge Cases")
        class EdgeCases {
                @Test
                void ping_emptyUrl_returns400() throws Exception {
                        mockMvc.perform(get("/api/ping")
                                        .param("url", "")
                                        .header("X-Test-Header", "ok"))
                                        .andExpect(status().isBadRequest())
                                        .andExpect(content()
                                                        .string(containsString("Missing or empty 'url' parameter")));
                }
        }
}
