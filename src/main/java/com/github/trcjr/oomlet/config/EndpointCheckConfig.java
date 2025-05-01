package com.github.trcjr.oomlet.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import com.github.trcjr.oomlet.HttpEndpointCheck;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.function.Supplier;

@Configuration
public class EndpointCheckConfig {

    @Bean
    public Supplier<List<HttpEndpointCheck>> endpointCheckSupplier() {
        return () -> {
            try {
                Path path = Paths.get("/opt/endpoint_health_indicator_config.yml");
                if (!Files.exists(path)) {
                    return List.of();
                }
                ObjectMapper mapper = new YAMLMapper();
                try (InputStream in = Files.newInputStream(path)) {
                    return List.of(mapper.readValue(in, HttpEndpointCheck[].class));
                }
            } catch (Exception e) {
                return List.of();
            }
        };
    }
}