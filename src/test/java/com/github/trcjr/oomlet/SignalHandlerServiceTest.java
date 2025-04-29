package com.github.trcjr.oomlet;

import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(MockitoExtension.class)
class SignalHandlerServiceTest {

    @Spy
    private SignalHandlerService signalHandlerService;

    @BeforeEach
    void setUp() {
        // No special setup needed
    }

    @Test
    void testSetupSignalHandlersDoesNotThrow() {
        signalHandlerService.setupSignalHandlers();
        // If no exception, we assume OK — signal handlers register safely
    }

    @Test
    void testOnShutdown() {
        signalHandlerService.onShutdown();
    }

    @Test
    void testOnSignalSIGINT() {
        doNothing().when(signalHandlerService).shutdownApplication();
        signalHandlerService.onSignal("INT");
        verify(signalHandlerService, times(1)).shutdownApplication();
    }

    @Test
    void testOnSignalSIGTERM() {
        doNothing().when(signalHandlerService).shutdownApplication();
        signalHandlerService.onSignal("TERM");
        verify(signalHandlerService, times(1)).shutdownApplication();
    }

    @Test
    void testOnSignalSIGHUP() {
        signalHandlerService.onSignal("HUP");
        // No shutdown expected, only logging
        verify(signalHandlerService, never()).shutdownApplication();
    }

    @Test
    void testOnSignalSIGQUIT() {
        doNothing().when(signalHandlerService).shutdownApplication();
        signalHandlerService.onSignal("QUIT");
        verify(signalHandlerService, times(1)).shutdownApplication();
    }

    @Test
    void testOnSignalSIGUSR1() {
        signalHandlerService.onSignal("USR1");
        verify(signalHandlerService, never()).shutdownApplication();
    }

    @Test
    void testOnSignalSIGUSR2() {
        signalHandlerService.onSignal("USR2");
        verify(signalHandlerService, never()).shutdownApplication();
    }

    @Test
    void testOnUnknownSignal() {
        signalHandlerService.onSignal("FAKESIGNAL");
        verify(signalHandlerService, never()).shutdownApplication();
    }
}