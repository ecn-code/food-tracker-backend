package com.eliascanalesnieto.foodtracker.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Import(MockConfig.class)
class AppConfigTest {

    @Autowired
    private AppConfig appConfig;

    @Test
    void checkParams() {
        assertThat(appConfig)
                .usingRecursiveComparison()
                .ignoringFields("dynamo.endpoint")
                .isEqualTo(
                        new AppConfig(
                                new CryptoConfig("E/bl0m55SLr1CFpHawud7nqV4oflUIi5PlEEy0RFAxI=", 5000),
                                new DynamoDBConfig("http", "food-tracker", "food-tracker-v2")
                        )
                );
    }

}