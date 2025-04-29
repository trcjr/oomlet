package com.github.trcjr.oomlet;

import java.util.Map;

public class HttpEndpointCheck {
    private String uri;
    private String method;
    private Map<String, String> headers;
    private String payload;

    public HttpEndpointCheck() {
    }

    public HttpEndpointCheck(String uri, String method, Map<String, String> headers, String payload) {
        this.uri = uri;
        this.method = method;
        this.headers = headers;
        this.payload = payload;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }
}