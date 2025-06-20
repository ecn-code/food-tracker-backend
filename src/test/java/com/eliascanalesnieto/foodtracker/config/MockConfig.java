package com.eliascanalesnieto.foodtracker.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

@TestConfiguration
@Slf4j
public class MockConfig {

    private static final LocalStackContainer localStackContainer;

    static {
        localStackContainer = new LocalStackContainer(DockerImageName.parse("localstack/localstack:3.2"))
                .withCopyFileToContainer(MountableFile.forClasspathResource("dynamodb/init-dynamodb.sh", 0744), "/etc/localstack/init/ready.d/init-dynamodb.sh")
                .withServices(LocalStackContainer.Service.DYNAMODB)
                .waitingFor(Wait.forLogMessage(".*Executed init-dynamodb.sh.*", 1));

        String[] files = {
                "food-tracker-v2.json",
                "user-data.json",
                "unit-data.json",
                "nutritional-information-data.json",
                "recipe-data.json",
                "product-data.json",
                "menu-data.json",
                "setting-data.json"
        };

        for (String file : files) {
            localStackContainer.withCopyFileToContainer(
                    MountableFile.forClasspathResource("dynamodb/" + file),
                    "/etc/localstack/init/ready.d/" + file
            );
        }

        localStackContainer.start();
        localStackContainer.followOutput(new Slf4jLogConsumer(log));
    }

    @Primary
    @Bean
    public IEnvService envServiceMock() {
        return key -> """
                    {
                        "crypto": { "key": "E/bl0m55SLr1CFpHawud7nqV4oflUIi5PlEEy0RFAxI=","expirationTimeMillis": 5000},
                        "dynamo": {"endpoint": "%s", "oldTableName": "food-tracker", "tableName": "food-tracker-v2"}
                    }
                """.formatted(localStackContainer.getEndpoint());
    }
}