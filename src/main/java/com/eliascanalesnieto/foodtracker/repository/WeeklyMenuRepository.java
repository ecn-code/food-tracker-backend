package com.eliascanalesnieto.foodtracker.repository;

import com.eliascanalesnieto.foodtracker.dto.out.WeeklyMenuResponse;
import com.eliascanalesnieto.foodtracker.entity.UserDynamo;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;

@Repository
public class WeeklyMenuRepository {

    private final DynamoDbTable<UserDynamo> dynamoDbTable;

    public WeeklyMenuRepository(final DynamoClient dynamoClient) {
        this.dynamoDbTable = dynamoClient.createTable(UserDynamo.TABLE_SCHEMA);
    }

    public WeeklyMenuResponse get(final String username, final String yearWeek) {
        return null;
    }
}
