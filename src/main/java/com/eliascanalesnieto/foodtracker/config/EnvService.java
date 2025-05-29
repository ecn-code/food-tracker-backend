package com.eliascanalesnieto.foodtracker.config;

import org.springframework.stereotype.Component;

@Component
public class EnvService implements IEnvService {

    public String getEnv(final String key) {
        return System.getenv(key);
    }

}
