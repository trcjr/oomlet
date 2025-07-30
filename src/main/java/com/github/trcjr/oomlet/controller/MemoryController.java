package com.github.trcjr.oomlet.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.github.trcjr.oomlet.service.MemoryService;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class MemoryController {

    private static final Logger logger = LoggerFactory.getLogger(MemoryController.class);

    private final MemoryService memoryService;

    @Autowired
    public MemoryController(MemoryService memoryService) {
        this.memoryService = memoryService;
    }

    @GetMapping("/allocate-memory")
    public Map<String, Object> allocateMemory(@RequestParam(name = "bytes") long bytes) {
        return memoryService.allocateMemory(bytes);
    }
}
