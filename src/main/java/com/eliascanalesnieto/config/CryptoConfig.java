package com.eliascanalesnieto.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

public record CryptoConfig(String key, String algorithm) {
}
