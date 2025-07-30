package com.github.trcjr.oomlet.service;

import java.util.Map;

public interface MemoryService {
    Map<String, Object> allocateMemory(long bytes);
}
