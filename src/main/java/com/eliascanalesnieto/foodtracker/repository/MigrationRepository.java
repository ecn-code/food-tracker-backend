package com.eliascanalesnieto.foodtracker.repository;

import com.eliascanalesnieto.foodtracker.config.AppConfig;
import com.eliascanalesnieto.foodtracker.entity.MenuDataDynamo;
import com.eliascanalesnieto.foodtracker.entity.MenuDynamo;
import com.eliascanalesnieto.foodtracker.entity.NutritionalInformationDataDynamo;
import com.eliascanalesnieto.foodtracker.entity.NutritionalInformationDynamo;
import com.eliascanalesnieto.foodtracker.entity.ProductDynamo;
import com.eliascanalesnieto.foodtracker.entity.ProductValueDynamo;
import com.eliascanalesnieto.foodtracker.entity.RecipeDynamo;
import com.eliascanalesnieto.foodtracker.entity.SettingDataDynamo;
import com.eliascanalesnieto.foodtracker.entity.SettingDynamo;
import com.eliascanalesnieto.foodtracker.entity.UserDataDynamo;
import com.eliascanalesnieto.foodtracker.entity.UserDynamo;
import com.eliascanalesnieto.foodtracker.entity.WeeklyMenuDataDynamo;
import com.eliascanalesnieto.foodtracker.entity.WeeklyMenuDynamo;
import com.eliascanalesnieto.foodtracker.entity.old.NutritionalValueOldDynamo;
import com.eliascanalesnieto.foodtracker.entity.old.SettingsV1OldDynamo;
import com.eliascanalesnieto.foodtracker.entity.old.UserOldDynamo;
import com.eliascanalesnieto.foodtracker.entity.old.WeeklyOldDynamo;
import com.eliascanalesnieto.foodtracker.service.HashService;
import com.eliascanalesnieto.foodtracker.utils.DateFormat;
import com.eliascanalesnieto.foodtracker.utils.IdFormat;
import com.fasterxml.jackson.databind.JsonNode;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class MigrationRepository {

    private final DynamoClient dynamoClient;
    private final AppConfig appConfig;
    private final HashService hashService;
    private final ProductRepository productRepository;
    private final RecipeRepository recipeRepository;

    public void migrateUsers() {
        final DynamoDbTable<UserDynamo> table = dynamoClient.createTable(UserDynamo.TABLE_SCHEMA);
        if (exist(UserDynamo.KEY, table)) {
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
                log.debug("Migrating from: " + user);

                final UserDynamo u = new UserDynamo();
                u.setType(UserDynamo.KEY.partitionKeyValue().s());
                u.setUsername(user.getSk());
                final UserDataDynamo userDataDynamo = new UserDataDynamo();
                u.setData(userDataDynamo);
                //userDataDynamo.setLastCode(hashService.hash(Keys.hmacShaKeyFor(Base64.getDecoder().decode(appConfig.crypto().key())), ""));
                log.debug("Migrating to: " + u);
                table.putItem(u);
            }
        });
        log.debug("Users migrated");
    }

    public void migrateWeeklyMenus() {
        log.debug("Migrating WeeklyMenus");
        final DynamoDbTable<WeeklyOldDynamo> oldTable = dynamoClient.createTable(appConfig.dynamo().oldTableName(), WeeklyOldDynamo.TABLE_SCHEMA);

        Key key = Key.builder()
                .partitionValue("weekly_menu#elias")
                .build();

        PageIterable<WeeklyOldDynamo> results = oldTable.query(r -> r.queryConditional(QueryConditional.keyEqualTo(key)));
        results.stream().forEach(page -> {
            for (WeeklyOldDynamo weeklyOldDynamo : page.items()) {
                log.debug("From " + weeklyOldDynamo);
                final WeeklyMenuDynamo weeklyMenuDynamo = new WeeklyMenuDynamo();
                weeklyMenuDynamo.setYearWeek(weeklyOldDynamo.getSk());
                weeklyMenuDynamo.setUsername(weeklyOldDynamo.getUsername());
                final WeeklyMenuDataDynamo weeklyMenuDataDynamo = new WeeklyMenuDataDynamo();
                weeklyMenuDynamo.setData(weeklyMenuDataDynamo);
                for (JsonNode menu : weeklyOldDynamo.getMenus()) {
                    final MenuDynamo menuDynamo = new MenuDynamo();
                    try {
                        menuDynamo.setDate(DateFormat.parse(menu.get("date").asText()));
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                    menuDynamo.setUsername(menu.get("username").asText());
                    final MenuDataDynamo menuDataDynamo = new MenuDataDynamo();
                    final Map<String, List<ProductValueDynamo>> products = new HashMap<>();

                    final JsonNode oldProducts = menu.get("products");
                    final Iterator<String> partsOfDay = oldProducts.fieldNames();

                    while (partsOfDay.hasNext()) {
                        String partOfDay = partsOfDay.next();
                        JsonNode items = oldProducts.get(partOfDay);

                        final ArrayList<ProductValueDynamo> productValueDynamos = new ArrayList<>();
                        for (JsonNode product : items) {
                            System.out.println(product);
                            String name = product.has("name") ? product.get("name").asText() : null;
                            String value = product.has("value") ? product.get("value").asText() : null;
                            if (name == null || value == null) throw new RuntimeException("Name or value null");

                            String recipeName = product.has("recipe_name") && product.get("recipe_name").isTextual() ? product.get("recipe_name").asText() : null;
                            final ProductValueDynamo productValueDynamo = new ProductValueDynamo();

                            ProductDynamo productDynamo = productRepository.get().stream()
                                    .filter(p -> p.getData() != null && name.equalsIgnoreCase(p.getData().getName()))
                                    .findFirst().orElseThrow();
                            productValueDynamo.setId(productDynamo.getId());

                            productValueDynamo.setName(name);
                            productValueDynamo.setQuantity(Double.valueOf(value));

                            if (recipeName != null) {
                                productValueDynamo.setUnit(productValueDynamo.getQuantity() > 1 ? "portions" : "portion");

                                RecipeDynamo recipeDynamo = recipeRepository.get().stream()
                                        .filter(r -> r.getData() != null && recipeName.equalsIgnoreCase(r.getData().getName()))
                                        .findFirst().orElseThrow();
                                productValueDynamo.setRecipeId(recipeDynamo.getId());
                            }

                            productValueDynamos.add(productValueDynamo);
                        }
                        products.put(partOfDay, productValueDynamos);
                    }
                    menuDataDynamo.setProducts(products);
                    menuDynamo.setData(menuDataDynamo);
                }

                log.debug("To " + weeklyMenuDynamo);
            }
        });
        log.debug("WeeklyMenus migrated");
    }

    public void migrateNutritionalValue() {
        log.debug("Migrating NutritionalValue");
        final DynamoDbTable<NutritionalInformationDynamo> table = dynamoClient.createTable(NutritionalInformationDynamo.TABLE_SCHEMA);
        if (exist(NutritionalInformationDynamo.KEY, table)) {
            log.debug("Not possible to migrate NutritionalValue because they exist");
            print(NutritionalInformationDynamo.KEY, table);
            return;
        }

        final DynamoDbTable<NutritionalValueOldDynamo> oldTable = dynamoClient.createTable(appConfig.dynamo().oldTableName(), NutritionalValueOldDynamo.TABLE_SCHEMA);

        Key key = Key.builder()
                .partitionValue("nutritional_value")
                .build();

        PageIterable<NutritionalValueOldDynamo> results = oldTable.query(r -> r.queryConditional(QueryConditional.keyEqualTo(key)));
        results.stream().forEach(page -> {
            for (NutritionalValueOldDynamo nutritionalValueOldDynamo : page.items()) {

                final NutritionalInformationDynamo nutritionalInformationDynamo = new NutritionalInformationDynamo();
                nutritionalInformationDynamo.setId(IdFormat.createId());
                nutritionalInformationDynamo.setType(NutritionalInformationDynamo.KEY.partitionKeyValue().s());

                final NutritionalInformationDataDynamo nutritionalInformationDataDynamo = new NutritionalInformationDataDynamo();
                nutritionalInformationDynamo.setData(nutritionalInformationDataDynamo);
                nutritionalInformationDataDynamo.setName(nutritionalValueOldDynamo.getName());
                nutritionalInformationDataDynamo.setShortName(nutritionalValueOldDynamo.getSk());
                nutritionalInformationDataDynamo.setUnit(nutritionalValueOldDynamo.getUnit());

                table.putItem(nutritionalInformationDynamo);

                log.debug("Migrating " + nutritionalInformationDynamo);
            }
        });
        log.debug("NutritionalValue migrated");
    }

    public void migrateSettings() {
        log.debug("Migrating settings");

        final DynamoDbTable<SettingDynamo> table = dynamoClient.createTable(SettingDynamo.TABLE_SCHEMA);
        if (exist(SettingDynamo.KEY, table)) {
            log.debug("Not possible to migrate Setting because they exist");
            print(SettingDynamo.KEY, table);
            return;
        }

        final DynamoDbTable<SettingsV1OldDynamo> oldTable = dynamoClient.createTable(appConfig.dynamo().oldTableName(), SettingsV1OldDynamo.TABLE_SCHEMA);
        Key key = Key.builder()
                .partitionValue("settings")
                .build();

        PageIterable<SettingsV1OldDynamo> results = oldTable.query(r -> r.queryConditional(QueryConditional.keyEqualTo(key)));
        results.stream().forEach(page -> {
            for (SettingsV1OldDynamo settingsV1OldDynamo : page.items()) {
                final SettingDynamo settingDynamo = new SettingDynamo();
                final SettingDataDynamo settingDataDynamo = new SettingDataDynamo();
                settingDynamo.setData(settingDataDynamo);
                settingDynamo.setVersion("V2");
                settingDataDynamo.setPartsOfDay(List.of(settingsV1OldDynamo.getSettings().get("partsOfDay").asText().split(",")));
                table.putItem(settingDynamo);
            }
        });

        log.debug("Settings migrated");
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