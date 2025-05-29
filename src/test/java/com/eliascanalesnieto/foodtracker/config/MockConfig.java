package com.eliascanalesnieto.foodtracker.config;

import com.eliascanalesnieto.foodtracker.repository.DynamoClient;
import com.eliascanalesnieto.foodtracker.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.ListTablesResponse;

import static org.testcontainers.containers.localstack.LocalStackContainer.Service.DYNAMODB;

@TestConfiguration
@Slf4j
public class MockConfig {

    private static final LocalStackContainer localStackContainer;

    static {
        localStackContainer = new LocalStackContainer(DockerImageName.parse("localstack/localstack:3.2"))
                .withCopyFileToContainer(MountableFile.forClasspathResource("dynamodb/food-tracker-v2.json"), "/etc/localstack/init/ready.d/food-tracker-v2.json")
                .withCopyFileToContainer(MountableFile.forClasspathResource("dynamodb/user-data.json"), "/etc/localstack/init/ready.d/user-data.json")
                .withCopyFileToContainer(MountableFile.forClasspathResource("dynamodb/init-dynamodb.sh", 0744), "/etc/localstack/init/ready.d/init-dynamodb.sh")
                .withServices(LocalStackContainer.Service.DYNAMODB)
                .waitingFor(Wait.forLogMessage(".*Executed init-dynamodb.sh.*", 1));
        localStackContainer.start();
        localStackContainer.followOutput(new Slf4jLogConsumer(log));
    }

    @Primary
    @Bean
    public IEnvService envServiceMock() {
        return key -> """
                    {
                        "crypto": { "key": "E/bl0m55SLr1CFpHawud7nqV4oflUIi5PlEEy0RFAxI=","expirationTimeMillis":2},
                        "dynamo": {"endpoint": "%s", "oldTableName": "food-tracker", "tableName": "food-tracker-v2"}
                    }
                """.formatted(localStackContainer.getEndpoint());
    }
}