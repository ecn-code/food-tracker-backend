package com.eliascanalesnieto.foodtracker.repository;

import com.eliascanalesnieto.foodtracker.config.AppConfig;
import com.eliascanalesnieto.foodtracker.entity.UserDynamo;
import com.eliascanalesnieto.foodtracker.entity.old.UserOldDynamo;
import com.eliascanalesnieto.foodtracker.entity.old.WeeklyDynamoOld;
import com.eliascanalesnieto.foodtracker.service.HashService;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class MigrationRepository {

    private final DynamoClient dynamoClient;
    private final AppConfig appConfig;
    private final HashService hashService;

    public void migrateUsers() {
        final DynamoDbTable<UserDynamo> table = dynamoClient.createTable(UserDynamo.TABLE_SCHEMA);
        if(exist(UserDynamo.KEY, table)) {
            log.debug("Not possible to migrate users because they exist");
            print(UserDynamo.KEY, table);
            return;
        }

        log.debug("Migrating users");
        final DynamoDbTable<UserOldDynamo> oldTable = dynamoClient.createTable(appConfig.dynamo().oldTableName(), UserOldDynamo.TABLE_SCHEMA);

        Key key = Key.builder()
                .partitionValue("user")
                .build();

        PageIterable<UserOldDynamo> results = oldTable.query(r -> r.queryConditional(QueryConditional.keyEqualTo(key)));
        results.stream().forEach(page -> {
            for (UserOldDynamo user : page.items()) {
                log.debug("Migrating " + user);

                final UserDynamo u = new UserDynamo();
                u.setType(UserDynamo.KEY.partitionKeyValue().s());
                u.setUsername(user.getSk());
                table.putItem(u);
            }
        });
        log.debug("Users migrated");
    }

    public void migrateWeeklyMenus() {
        log.debug("Migrating WeeklyMenus");
        final DynamoDbTable<WeeklyDynamoOld> oldTable = dynamoClient.createTable(appConfig.dynamo().oldTableName(), WeeklyDynamoOld.TABLE_SCHEMA);

        Key key = Key.builder()
                .partitionValue("weekly_menu#elias")
                .build();

        PageIterable<WeeklyDynamoOld> results = oldTable.query(r -> r.queryConditional(QueryConditional.keyEqualTo(key)));
        results.stream().forEach(page -> {
            for (WeeklyDynamoOld weeklyDynamoOld : page.items()) {
                log.debug("Migrating " + weeklyDynamoOld);
            }
        });
        log.debug("WeeklyMenus migrated");
    }

    private void print(final Key key, final DynamoDbTable<?> table) {
        PageIterable<?> results = table.query(r -> r.queryConditional(QueryConditional.keyEqualTo(key)));

        results.stream().forEach(page -> {
            for (Object user : page.items()) {
                System.out.println(user);
            }
        });
    }

    private boolean exist(final Key key, final DynamoDbTable<?> table) {
        return table.query(r -> r
                .queryConditional(QueryConditional.keyEqualTo(key))
        ).stream().anyMatch(page -> !page.items().isEmpty());
    }

}
