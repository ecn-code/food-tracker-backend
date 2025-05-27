package com.eliascanalesnieto.repository;

import com.eliascanalesnieto.config.AppConfig;
import com.eliascanalesnieto.entity.UserDynamo;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.net.URI;

@Repository
public class DynamoDBClient {

    public DynamoDBClient(final AppConfig appConfig) {
        DynamoDbClient standardClient = DynamoDbClient.builder()
                .credentialsProvider(ProfileCredentialsProvider.create())
                .endpointOverride(URI.create(appConfig.dynamo().endpoint()))
                .build();

        DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder()
                .dynamoDbClient(standardClient)
                .build();

        final DynamoDbTable<UserDynamo> table = enhancedClient.table("food-tracker", UserDynamo.TABLE_SCHEMA);

        Key key = Key.builder()
                .partitionValue("user")
                .build();

        PageIterable<UserDynamo> results = table.query(r -> r.queryConditional(QueryConditional.keyEqualTo(key)));

        results.stream().forEach(page -> {
            for (UserDynamo user : page.items()) {
                System.out.println(user);
            }
        });
    }

}
