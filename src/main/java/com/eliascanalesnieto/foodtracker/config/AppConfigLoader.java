package com.eliascanalesnieto.foodtracker.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
class AppConfigLoader {

    private static final String APP_CONFIG = "APP_CONFIG";
    private final IEnvService envService;

    @Bean
    public AppConfig createAppConfig(final ObjectMapper objectMapper) throws JsonProcessingException {
        final String rawJson = envService.getEnv(APP_CONFIG);
        return objectMapper.readValue(rawJson, AppConfig.class);
    }
}
