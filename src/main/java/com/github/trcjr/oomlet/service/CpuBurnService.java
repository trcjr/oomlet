package com.github.trcjr.oomlet.service;

import com.github.trcjr.oomlet.dto.CpuBurnResponse;

public interface CpuBurnService {
    CpuBurnResponse burnCpu(long millis, int threads);
}
