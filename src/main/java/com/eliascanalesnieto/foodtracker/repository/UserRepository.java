package com.eliascanalesnieto.foodtracker.repository;

import com.eliascanalesnieto.foodtracker.entity.UserDynamo;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Expression;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.Map;
import java.util.Optional;

@Repository
public class UserRepository {

    private final DynamoDbTable<UserDynamo> dynamoDbTable;

    public UserRepository(final DynamoClient dynamoClient) {
        this.dynamoDbTable = dynamoClient.createTable(UserDynamo.TABLE_SCHEMA);
    }

    public Optional<UserDynamo> get(final String username, final String code) {
        return dynamoDbTable.query(r -> r
                        .queryConditional(QueryConditional.keyEqualTo(
                                UserDynamo.KEY.toBuilder().sortValue(username).build())
                        ).filterExpression(
                                Expression.builder()
                                        .expression("last_code = :code")
                                        .expressionValues(Map.of(":code", AttributeValue.fromS(code)))
                                        .build()
                        )
                )
                .stream()
                .flatMap(page -> page.items().stream())
                .findFirst();
    }

    public Optional<UserDynamo> get(final String username) {
        return dynamoDbTable.query(r -> r
                        .queryConditional(QueryConditional.keyEqualTo(
                                UserDynamo.KEY.toBuilder().sortValue(username).build())
                        )
                )
                .stream()
                .flatMap(page -> page.items().stream())
                .findFirst();
    }

    public void update(final UserDynamo userDynamo) {
        dynamoDbTable.updateItem(userDynamo);
    }

}
