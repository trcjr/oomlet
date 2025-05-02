package com.github.trcjr.oomlet.config;

import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.HttpClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

        private static final int CONNECT_TIMEOUT = 3000; // milliseconds
        private static final int READ_TIMEOUT = 5000; // milliseconds

        @Bean
        public RestTemplate restTemplate() {
                return new RestTemplate(new HttpClient5RequestFactory());
        }


}