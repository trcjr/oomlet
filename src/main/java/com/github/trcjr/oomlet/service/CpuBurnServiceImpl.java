package com.github.trcjr.oomlet.service;

import com.github.trcjr.oomlet.dto.CpuBurnResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CpuBurnServiceImpl implements CpuBurnService {

    private static final Logger logger = LoggerFactory.getLogger(CpuBurnServiceImpl.class);

    @Override
    public CpuBurnResponse burnCpu(long millis, int threads) {
        logger.info("Received CPU burn request: millis={} threads={}", millis, threads);

        long endTime = System.currentTimeMillis() + millis;
        List<Thread> workers = new ArrayList<>();

        for (int i = 0; i < threads; i++) {
            Thread worker = new Thread(() -> {
                while (System.currentTimeMillis() < endTime) {
                    double x = Math.random() * Math.random(); // Do something to waste CPU
                }
            });
            worker.start();
            workers.add(worker);
        }

        // Optionally wait for all threads to finish
        for (Thread worker : workers) {
            try {
                worker.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.error("CPU burn interrupted", e);
                return new CpuBurnResponse(millis, threads, "interrupted");
            }
        }

        logger.info("CPU burn complete");
        return new CpuBurnResponse(millis, threads, "completed");
    }
}
