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

    @PostConstruct
    public void setupSignalHandlers() {
        logger.info("Setting up OS signal handlers...");

        handleSignal("INT");    // Ctrl+C interrupt
        handleSignal("TERM");   // Termination (docker stop, k8s shutdown)
        handleSignal("HUP");    // Hangup (terminal closed, config reload trigger)
        handleSignal("QUIT");   // Quit (Ctrl+\)
        handleSignal("USR1");   // User-defined signal 1 (for future expansion)
        handleSignal("USR2");   // User-defined signal 2 (for future expansion)
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

    /**
     * Handles an incoming OS signal and initiates appropriate behavior.
     */
    protected void onSignal(String signalName) {
        switch (signalName) {
            case "INT":
                logger.info("SIGINT received: Graceful shutdown initiated.");
                shutdownApplication();
                break;
            case "TERM":
                logger.info("SIGTERM received: Graceful shutdown initiated.");
                shutdownApplication();
                break;
            case "HUP":
                logger.info("SIGHUP received: No-op (possible future reload trigger).");
                // Optional: implement reload configs without shutdown
                break;
            case "QUIT":
                logger.info("SIGQUIT received: Forced shutdown initiated.");
                shutdownApplication();
                break;
            case "USR1":
                logger.info("SIGUSR1 received: User-defined action (currently no-op).");
                // Optional: future custom behavior
                break;
            case "USR2":
                logger.info("SIGUSR2 received: User-defined action (currently no-op).");
                // Optional: future custom behavior
                break;
            default:
                logger.warn("Unknown signal {} received. No action taken.", signalName);
        }
    }

    /**
     * Isolated application shutdown method.
     * Allows for graceful termination and testing without real System.exit() in unit tests.
     */
    protected void shutdownApplication() {
        logger.info("Shutting down application now...");
        System.exit(0);
    }

    @PreDestroy
    public void onShutdown() {
        logger.info("Application shutdown triggered via @PreDestroy hook.");
    }
}