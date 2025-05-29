package com.eliascanalesnieto.foodtracker.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Import(MockConfig.class)
class AppConfigTest {

    @Autowired
    private AppConfig appConfig;

    @Test
    void checkParams() {
        assertEquals(
                new AppConfig(new CryptoConfig("E/bl0m55SLr1CFpHawud7nqV4oflUIi5PlEEy0RFAxI=", 2),
                        new DynamoDBConfig("http", "food")),
                appConfig
        );
    }

}