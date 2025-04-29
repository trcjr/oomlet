package com.github.trcjr.oomlet;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StatusController {
    @GetMapping("/api/status")
    public ResponseEntity<String> setStatus(@RequestParam(defaultValue = "200") int responseCode) {
        return ResponseEntity.status(responseCode).body("Returned status code: " + responseCode);
    }
}