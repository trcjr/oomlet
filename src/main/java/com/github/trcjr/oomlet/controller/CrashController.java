package com.github.trcjr.oomlet.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class CrashController {

    @PostMapping("/crash")
    public ResponseEntity<String> crash(@RequestParam(defaultValue = "1") int code) {
        new Thread(() -> {
            try {
                Thread.sleep(100); // let response flush
            } catch (InterruptedException ignored) {}
            System.exit(code);
        }).start();

        return ResponseEntity.ok("Crashing with exit code: " + code);
    }
}