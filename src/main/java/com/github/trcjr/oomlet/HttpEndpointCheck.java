package com.github.trcjr.oomlet;

import org.springframework.http.HttpMethod;

import java.util.Map;
import java.util.Objects;

/**
 * Represents an HTTP request to be used in endpoint health checks.
 */
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

    @Override
    public String toString() {
        return "HttpEndpointCheck{" +
                "uri='" + uri + '\'' +
                ", method='" + method + '\'' +
                ", headers=" + headers +
                ", payload='" + payload + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof HttpEndpointCheck)) return false;
        HttpEndpointCheck that = (HttpEndpointCheck) o;
        return Objects.equals(uri, that.uri) &&
                Objects.equals(method, that.method) &&
                Objects.equals(headers, that.headers) &&
                Objects.equals(payload, that.payload);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uri, method, headers, payload);
    }
}