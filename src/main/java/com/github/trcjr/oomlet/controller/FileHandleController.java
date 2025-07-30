package com.github.trcjr.oomlet.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.github.trcjr.oomlet.service.FileHandleService;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class FileHandleController {

    private static final Logger logger = LoggerFactory.getLogger(FileHandleController.class);

    private final FileHandleService fileHandleService;

    @Autowired
    public FileHandleController(FileHandleService fileHandleService) {
        this.fileHandleService = fileHandleService;
    }

    @GetMapping("/open-files")
    public Map<String, Object> openFiles(@RequestParam(name = "count") int count) {
        return fileHandleService.openFiles(count);
    }
}
