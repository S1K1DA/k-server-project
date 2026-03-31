package com.example.k_server_project.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "portone")
public class PortOneProperties {

    private Api api;
    private String storeId;
    private String channelKey;
    private Webhook webhook;

    @Data
    public static class Api {
        private String baseUrl;
        private String secret;
    }

    @Data
    public static class Webhook {
        private String secret;
    }
}