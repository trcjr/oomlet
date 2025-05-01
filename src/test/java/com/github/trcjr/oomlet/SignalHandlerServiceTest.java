package com.github.trcjr.oomlet;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.mockito.Mockito.*;

class SignalHandlerServiceTest {

    private SignalHandlerService service;

    @BeforeEach
    void setUp() {
        service = Mockito.spy(new SignalHandlerService() {
            @Override
            protected void shutdownApplication() {
                logger.info("Mock shutdown called.");
            }
        });
    }

    private static final Logger logger = LoggerFactory.getLogger(SignalHandlerServiceTest.class);

    @Test
    void testHandleIntSignal() {
        service.onSignal("INT");
        verify(service).shutdownApplication();
    }

    @Test
    void testHandleTermSignal() {
        service.onSignal("TERM");
        verify(service).shutdownApplication();
    }

    @Test
    void testHandleHupSignal() {
        service.onSignal("HUP");
        verify(service, never()).shutdownApplication();
    }

    @Test
    void testHandleQuitSignal() {
        service.onSignal("QUIT");
        verify(service).shutdownApplication();
    }

    @Test
    void testHandleUsr1Signal() {
        service.onSignal("USR1");
        verify(service, never()).shutdownApplication();
    }

    @Test
    void testHandleUsr2Signal() {
        service.onSignal("USR2");
        verify(service, never()).shutdownApplication();
    }

    @Test
    void testHandleUnknownSignal() {
        service.onSignal("UNKNOWN");
        verify(service, never()).shutdownApplication();
    }

    @Test
    void testOnShutdownLogsMessage() {
        service.onShutdown(); // No assertion needed, just ensures no exception
    }

    @Test
    void testSetupSignalHandlersRunsWithoutException() {
        service.setupSignalHandlers(); // Just ensuring it executes; platform-dependent behavior not asserted
    }
}