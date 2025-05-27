package com.eliascanalesnieto.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AppConfigTest {

    @Autowired
    private AppConfig appConfig;

    @Test
    void checkParams() {
        assertEquals(new AppConfig(new CryptoConfig("1", "2"), new DynamoDBConfig("3")), appConfig);
    }

}