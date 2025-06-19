package com.eliascanalesnieto.foodtracker.repository;

import com.eliascanalesnieto.foodtracker.entity.SettingDynamo;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;

import java.util.Optional;

@Repository
public class SettingRepository {

    private final DynamoDbTable<SettingDynamo> dynamoDbTable;

    public SettingRepository(final DynamoClient dynamoClient) {
        this.dynamoDbTable = dynamoClient.createTable(SettingDynamo.TABLE_SCHEMA);
    }

    public Optional<SettingDynamo> get(final String version) {
        return dynamoDbTable.query(r -> r
                        .queryConditional(QueryConditional.keyEqualTo(
                                SettingDynamo.KEY.toBuilder().sortValue(version).build())
                        )
                )
                .stream()
                .flatMap(page -> page.items().stream())
                .findFirst();
    }
}

