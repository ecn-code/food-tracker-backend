package com.eliascanalesnieto.foodtracker.config;

import com.eliascanalesnieto.foodtracker.repository.DynamoClient;
import com.eliascanalesnieto.foodtracker.repository.UserRepository;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class MockConfig {

    @Bean
    public DynamoClient dynamoClient() {
        return Mockito.mock(DynamoClient.class);
    }

    @Bean
    public UserRepository userRepository() {
        return Mockito.mock(UserRepository.class);
    }
}