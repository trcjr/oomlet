package com.github.trcjr.oomlet.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@RestController
@RequestMapping("/api/ping")
public class PingController {

    private static final Logger logger = LoggerFactory.getLogger(PingController.class);

    private final RestTemplate restTemplate;

    @Autowired
    public PingController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @GetMapping
    public ResponseEntity<String> ping(@RequestParam String url,
                                       @RequestHeader Map<String, String> headers) {
        logger.info("Ping request to: {}", url);

        if (url == null || url.isBlank()) {
            return ResponseEntity.badRequest().body("Missing or empty 'url' parameter");
        }

        try {
            HttpHeaders httpHeaders = new HttpHeaders();
            headers.forEach(httpHeaders::set);
            HttpEntity<Void> entity = new HttpEntity<>(httpHeaders);

            ResponseEntity<Void> response = restTemplate.exchange(url, HttpMethod.GET, entity, Void.class);

            logger.info("Ping successful: {} - {}", response.getStatusCodeValue(), response.getStatusCode());
            return ResponseEntity.ok("Ping successful with status: " + response.getStatusCodeValue());

        } catch (Exception e) {
            logger.error("Ping failed for URL: {}", url, e);
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body("Ping failed: " + e.getMessage());
        }
    }
}
