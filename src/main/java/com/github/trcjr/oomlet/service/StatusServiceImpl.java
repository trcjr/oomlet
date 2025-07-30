package com.github.trcjr.oomlet.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class StatusServiceImpl implements StatusService {

    private static final Logger logger = LoggerFactory.getLogger(StatusServiceImpl.class);
    private static final int MAX_STATUS = 599;
    private static final int MIN_STATUS = 100;
    private static final long MAX_DELAY_MS = 30_000;

    @Override
    public String setStatus(int code, long delayMillis) {
        logger.info("Received status request: code={}, delay={}ms", code, delayMillis);

        if (code < MIN_STATUS || code > MAX_STATUS) {
            logger.warn("Invalid status code: {}. Must be between {} and {}.", code, MIN_STATUS, MAX_STATUS);
            throw new IllegalArgumentException("Invalid status code. Must be between " + MIN_STATUS + " and " + MAX_STATUS + ".");
        }

        if (delayMillis < 0) {
            logger.warn("Negative delay: {}ms. Delay must be >= 0.", delayMillis);
            throw new IllegalArgumentException("Invalid delay. Must be >= 0.");
        }

        if (delayMillis > MAX_DELAY_MS) {
            logger.warn("Delay too long: {}ms. Max allowed is {}ms.", delayMillis, MAX_DELAY_MS);
            throw new IllegalArgumentException("Invalid delay. Max allowed is " + MAX_DELAY_MS + " ms.");
        }

        try {
            if (delayMillis > 0) {
                Thread.sleep(delayMillis);
            }

            return "Returning HTTP status: " + code + " after " + delayMillis + " ms";

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("Interrupted during delay", e);
            throw new RuntimeException("Interrupted during delay", e);
        }
    }
}
