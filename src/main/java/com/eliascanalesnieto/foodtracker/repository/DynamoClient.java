package com.eliascanalesnieto.foodtracker.repository;

import com.eliascanalesnieto.foodtracker.config.AppConfig;
import com.eliascanalesnieto.foodtracker.config.DynamoDBConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.net.URI;

@Slf4j
@Repository
public class DynamoClient {

    private final DynamoDbEnhancedClient enhancedClient;
    private final DynamoDbClient standardClient;
    private final DynamoDBConfig dynamoDBConfig;

    public DynamoClient(final AppConfig appConfig) {
        dynamoDBConfig = appConfig.dynamo();
        standardClient = DynamoDbClient.builder()
                .credentialsProvider(ProfileCredentialsProvider.create())
                .endpointOverride(URI.create(dynamoDBConfig.endpoint()))
                .build();

        enhancedClient = DynamoDbEnhancedClient.builder()
                .dynamoDbClient(standardClient)
                .build();
    }

    public <T> DynamoDbTable<T> createTable(final TableSchema<T> tableSchema) {
        return enhancedClient.table(dynamoDBConfig.tableName(), tableSchema);
    }
}
