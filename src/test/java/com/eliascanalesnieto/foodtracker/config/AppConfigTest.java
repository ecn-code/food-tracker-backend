package com.eliascanalesnieto.foodtracker.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;

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
                                "http://localhost:8080",
                                new CryptoConfig("E/bl0m55SLr1CFpHawud7nqV4oflUIi5PlEEy0RFAxI=", 5000),
                                new DynamoDBConfig("http", "food-tracker", "food-tracker-v2")
                        )
                );
    }

}