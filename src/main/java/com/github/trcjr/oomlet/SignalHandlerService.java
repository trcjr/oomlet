package com.github.trcjr.oomlet;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import sun.misc.Signal;

@Service
public class SignalHandlerService {

    private static final Logger logger = LoggerFactory.getLogger(SignalHandlerService.class);
    private final Runnable shutdownHook;

    // Default constructor: used in production
    public SignalHandlerService() {
        this(() -> System.exit(0));
    }

    // Testable constructor
    protected SignalHandlerService(Runnable shutdownHook) {
        this.shutdownHook = shutdownHook;
    }

    @PostConstruct
    public void setupSignalHandlers() {
        logger.info("Setting up OS signal handlers...");
        handleSignal("INT");
        handleSignal("TERM");
        handleSignal("HUP");
        handleSignal("QUIT");
        handleSignal("USR1");
        handleSignal("USR2");
    }

    private void handleSignal(String signalName) {
        try {
            Signal.handle(new Signal(signalName), signal -> {
                logger.info("Received OS signal: {}", signal.getName());
                onSignal(signal.getName());
            });
            logger.info("Registered handler for signal: {}", signalName);
        } catch (IllegalArgumentException e) {
            logger.warn("Signal {} not supported on this platform.", signalName);
        }
    }

    protected void onSignal(String signalName) {
        switch (signalName) {
            case "INT", "TERM", "QUIT" -> {
                logger.info("{} received: Graceful shutdown initiated.", signalName);
                shutdownApplication();
            }
            case "HUP" -> logger.info("SIGHUP received: No-op (possible future reload trigger).");
            case "USR1", "USR2" -> logger.info("SIG{} received: User-defined action (currently no-op).", signalName);
            default -> logger.warn("Unknown signal {} received. No action taken.", signalName);
        }
    }

    protected void shutdownApplication() {
        logger.info("Shutting down application now...");
        shutdownHook.run();  // Replaceable in test
    }

    @PreDestroy
    public void onShutdown() {
        logger.info("Application shutdown triggered via @PreDestroy hook.");
    }
}