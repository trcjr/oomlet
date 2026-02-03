package com.github.trcjr.oomlet.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.nio.charset.StandardCharsets;
import java.util.Random;

@RestController
@RequestMapping("/api")
public class RandomDataController {

    private static final Logger logger = LoggerFactory.getLogger(RandomDataController.class);
    private static final int DEFAULT_BUFFER = 8 * 1024; // 8 KB

    @GetMapping(value = "/random-data", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<StreamingResponseBody> random(@RequestParam(name = "size", defaultValue = "1024") long size) {
        if (size < 0) {
            logger.warn("Invalid size requested: {}", size);
            byte[] msg = "Invalid 'size' parameter".getBytes(StandardCharsets.UTF_8);
            return ResponseEntity.badRequest()
                    .contentType(MediaType.TEXT_PLAIN)
                    .contentLength(msg.length)
                    .body(outputStream -> outputStream.write(msg));
        }

        StreamingResponseBody stream = outputStream -> {
            byte[] buffer = new byte[DEFAULT_BUFFER];
            Random rnd = new Random();
            long remaining = size;

            while (remaining > 0) {
                int toWrite = (int) Math.min(buffer.length, remaining);
                rnd.nextBytes(buffer);
                outputStream.write(buffer, 0, toWrite);
                remaining -= toWrite;
            }
            outputStream.flush();
        };

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(size)
                .body(stream);
    }
}
