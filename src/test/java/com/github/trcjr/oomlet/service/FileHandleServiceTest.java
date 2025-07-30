package com.github.trcjr.oomlet.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FileHandleServiceTest {

    @Autowired
    private FileHandleService fileHandleService;

    @Test
    void testOpenFilesZero() {
        Map<String, Object> result = fileHandleService.openFiles(0);

        assertEquals(0, result.get("requested"));
        assertEquals(0, result.get("successfullyOpened"));
        assertEquals(0, result.get("failed"));
    }

    @Test
    void testOpenFilesSmall() {
        Map<String, Object> result = fileHandleService.openFiles(3);

        assertEquals(3, result.get("requested"));
        assertEquals(3, result.get("successfullyOpened"));
        assertEquals(0, result.get("failed"));
    }

    @Test
    void testOpenFilesSingle() {
        Map<String, Object> result = fileHandleService.openFiles(1);

        assertEquals(1, result.get("requested"));
        assertEquals(1, result.get("successfullyOpened"));
        assertEquals(0, result.get("failed"));
    }
}
