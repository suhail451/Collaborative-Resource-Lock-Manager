package com.suhail.collaborative_resource_lock_manager.Authentication;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AuthVaultConfig {

    @Value("${authvault.api-key}")
    private String apiKey;

    @Value("${authvault.client-id}")
    private String clientId;

    @Value("${authvault.validate-url}")
    private String validateUrl;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    public String getApiKey() {
        return apiKey;
    }

    public String getClientId() {
        return clientId;
    }

    public String getValidateUrl() {
        return validateUrl;
    }
}