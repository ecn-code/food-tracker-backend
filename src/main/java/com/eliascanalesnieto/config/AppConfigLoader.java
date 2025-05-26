package com.eliascanalesnieto.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class AppConfigLoader {

    private static final String APP_CONFIG = "APP_CONFIG";

    @Bean
    public AppConfig createAppConfig(final ObjectMapper objectMapper) throws JsonProcessingException {
        final String rawJson = System.getenv(APP_CONFIG);
        return objectMapper.readValue(rawJson, AppConfig.class);
    }
}
