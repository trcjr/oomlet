package com.github.trcjr.oomlet.config;

import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.core5.util.Timeout;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.HttpMethod;

import java.io.IOException;
import java.net.URI;
import java.net.HttpURLConnection;

/**
 * Basic wrapper for HttpClient5. Limited support — mainly GET/POST with basic timeout config.
 */
public class HttpClient5RequestFactory extends SimpleClientHttpRequestFactory implements ClientHttpRequestFactory {

    private final CloseableHttpClient httpClient;

    public HttpClient5RequestFactory() {
        RequestConfig config = RequestConfig.custom()
                .setConnectionRequestTimeout(Timeout.ofSeconds(3))
                .setConnectTimeout(Timeout.ofSeconds(3))
                .setResponseTimeout(Timeout.ofSeconds(5))
                .build();

        this.httpClient = HttpClients.custom()
                .setDefaultRequestConfig(config)
                .build();
    }

    @Override
    protected void prepareConnection(HttpURLConnection connection, String httpMethod) throws IOException {
        connection.setConnectTimeout(3000);
        connection.setReadTimeout(5000);
        super.prepareConnection(connection, httpMethod);
    }
}