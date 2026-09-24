package com.github.trcjr.oomlet;

import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;

import static org.junit.jupiter.api.Assertions.assertEquals;

class KubernetesConfigTest {

    @Configuration(proxyBeanMethods = false)
    @EnableAutoConfiguration
    static class TestApplication {
    }

    @Test
    void importsConfigMapFromConfiguredNamespace() throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        try {
            String configMaps = """
                    {"apiVersion":"v1","kind":"ConfigMapList","items":[
                      {"apiVersion":"v1","kind":"ConfigMap",
                       "metadata":{"name":"oomlet-test-config","namespace":"test"},
                       "data":{"application.properties":"oomlet.test.message=from-kubernetes"}}
                    ]}
                    """;
            server.createContext("/api/v1/namespaces/test/configmaps", exchange -> {
                byte[] body = configMaps.getBytes(StandardCharsets.UTF_8);
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, body.length);
                try (var output = exchange.getResponseBody()) {
                    output.write(body);
                }
            });
            server.start();
            SpringApplication application = new SpringApplication(TestApplication.class);
            application.setWebApplicationType(WebApplicationType.NONE);
            try (var context = application.run(
                    "--spring.main.cloud-platform=kubernetes",
                    "--spring.config.import=kubernetes:",
                    "--spring.cloud.kubernetes.config.enabled=true",
                    "--spring.cloud.kubernetes.config.name=oomlet-test-config",
                    "--spring.cloud.kubernetes.config.namespace=test",
                    "--spring.cloud.kubernetes.client.master-url=http://127.0.0.1:" + server.getAddress().getPort(),
                    "--spring.cloud.kubernetes.client.namespace=test",
                    "--spring.cloud.kubernetes.client.try-kube-config=false",
                    "--spring.cloud.kubernetes.client.try-service-account=false")) {
                assertEquals("from-kubernetes", context.getEnvironment().getProperty("oomlet.test.message"));
            }
        } finally {
            server.stop(0);
        }
    }
}
