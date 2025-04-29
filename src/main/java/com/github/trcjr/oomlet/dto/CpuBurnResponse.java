package com.github.trcjr.oomlet.dto;

public class CpuBurnResponse {
    private long requestedMillis;
    private int requestedThreads;
    private String status;

    public CpuBurnResponse() {
    }

    public CpuBurnResponse(long requestedMillis, int requestedThreads, String status) {
        this.requestedMillis = requestedMillis;
        this.requestedThreads = requestedThreads;
        this.status = status;
    }

    public long getRequestedMillis() {
        return requestedMillis;
    }

    public void setRequestedMillis(long requestedMillis) {
        this.requestedMillis = requestedMillis;
    }

    public int getRequestedThreads() {
        return requestedThreads;
    }

    public void setRequestedThreads(int requestedThreads) {
        this.requestedThreads = requestedThreads;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}