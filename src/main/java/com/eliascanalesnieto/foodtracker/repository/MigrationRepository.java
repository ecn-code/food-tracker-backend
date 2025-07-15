package com.eliascanalesnieto.foodtracker.repository;

import com.eliascanalesnieto.foodtracker.config.AppConfig;
import com.eliascanalesnieto.foodtracker.entity.*;
import com.eliascanalesnieto.foodtracker.entity.old.NutritionalValueOldDynamo;
import com.eliascanalesnieto.foodtracker.entity.old.ProductOldDynamo;
import com.eliascanalesnieto.foodtracker.entity.old.RecipeOldDynamo;
import com.eliascanalesnieto.foodtracker.entity.old.SettingsV1OldDynamo;
import com.eliascanalesnieto.foodtracker.entity.old.UnitOldDynamo;
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
import org.springframework.util.StringUtils;
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

@Slf4j
@Repository
@RequiredArgsConstructor
public class MigrationRepository {

    private final DynamoClient dynamoClient;
    private final AppConfig appConfig;
    private final HashService hashService;
    private final NutritionalInformationRepository nutritionalInformationRepository;

    private final Map<String, ProductDynamo> migratedProducts = new HashMap<>();
    private final Map<String, RecipeDynamo> migratedRecipes = new HashMap<>();

    public void migrateUnits() {
        log.debug("Migrating Units");
        final DynamoDbTable<UnitDynamo> table = dynamoClient.createTable(UnitDynamo.TABLE_SCHEMA);
        if (exist(UnitDynamo.KEY, table)) {
            log.debug("Not possible to migrate Units because they exist");
            print(UnitDynamo.KEY, table);
            return;
        }

        final DynamoDbTable<UnitOldDynamo> oldTable = dynamoClient.createTable(appConfig.dynamo().oldTableName(), UnitOldDynamo.TABLE_SCHEMA);

        Key key = Key.builder()
                .partitionValue("unit")
                .build();

        PageIterable<UnitOldDynamo> results = oldTable.query(r -> r.queryConditional(QueryConditional.keyEqualTo(key)));
        results.stream().forEach(page -> {
            for (UnitOldDynamo unitOldDynamo : page.items()) {

                final UnitDynamo unitDynamo = new UnitDynamo();
                unitDynamo.setId(IdFormat.createId());
                unitDynamo.setType(UnitDynamo.KEY.partitionKeyValue().s());

                final UnitDataDynamo unitDataDynamo = new UnitDataDynamo();
                unitDynamo.setData(unitDataDynamo);
                unitDataDynamo.setName(unitOldDynamo.getName());
                unitDataDynamo.setShortName(unitOldDynamo.getSk());
                unitDataDynamo.setShortName(unitOldDynamo.getShortName());

                table.putItem(unitDynamo);

                log.debug("Migrating unit " + unitDynamo);
            }
        });
        log.debug("Units migrated");
    }

    public void migrateProductsAndRecipes() {
        migrateProductsPrivate();
        migrateRecipesPrivate();

        for (ProductDynamo product : migratedProducts.values()) {
            if (StringUtils.hasText(product.getData().getRecipeId())) {
                final RecipeDynamo recipeDynamo = migratedRecipes.get(product.getData().getRecipeId().toLowerCase());
                if (recipeDynamo == null) throw new RuntimeException("Recipe null: " + product);
                product.getData().setRecipeId(recipeDynamo.getId());
            }
        }

        for (RecipeDynamo recipe : migratedRecipes.values()) {
            for (ProductValueDynamo productValue : recipe.getData().getProducts()) {
                if (productValue.getName().equals("seitan 1/4")) {
                    productValue.setName("seitan 1/8");
                }
                final ProductDynamo productDynamo = migratedProducts.get(productValue.getName());
                if (productDynamo == null) {
                    throw new RuntimeException("ProductDynamo null: " + productValue.getName());
                }
                productValue.setName(productDynamo.getData().getName());
                productValue.setId(productDynamo.getId());
                productValue.setUnit("g");
                productValue.setRecipeId(productDynamo.getData().getRecipeId());
                if (StringUtils.hasText(productValue.getRecipeId())) {
                    productValue.setUnit(productValue.getValue() > 1 ? "portions" : "portion");
                }
            }
        }

        final DynamoDbTable<ProductDynamo> tableProduct = dynamoClient.createTable(ProductDynamo.TABLE_SCHEMA);
        for (ProductDynamo product : migratedProducts.values()) {
            tableProduct.putItem(product);
        }

        final DynamoDbTable<RecipeDynamo> tableRecipe = dynamoClient.createTable(RecipeDynamo.TABLE_SCHEMA);
        for (RecipeDynamo recipe : migratedRecipes.values()) {
            tableRecipe.putItem(recipe);
        }
    }

    private void migrateProductsPrivate() {
        final DynamoDbTable<ProductDynamo> table = dynamoClient.createTable(ProductDynamo.TABLE_SCHEMA);
        if (exist(ProductDynamo.KEY, table)) {
            log.debug("Not possible to migrate products because they exist");
            print(ProductDynamo.KEY, table);
            return;
        }

        log.debug("Migrating products");
        final DynamoDbTable<ProductOldDynamo> oldTable = dynamoClient.createTable(appConfig.dynamo().oldTableName(), ProductOldDynamo.TABLE_SCHEMA);

        Key key = Key.builder()
                .partitionValue("product")
                .build();

        PageIterable<ProductOldDynamo> results = oldTable.query(r -> r.queryConditional(QueryConditional.keyEqualTo(key)));
        results.stream().forEach(page -> {
            for (ProductOldDynamo productOld : page.items()) {
                log.debug("Migrating product from: " + productOld);

                final ProductDynamo productDynamo = new ProductDynamo();
                productDynamo.setId(IdFormat.createId());
                final ProductDataDynamo productDataDynamo = new ProductDataDynamo();
                productDataDynamo.setDescription(productOld.getDescription());
                productDataDynamo.setName(productOld.getName());
                productDataDynamo.setRecipeId(productOld.getRecipeName());
                productDataDynamo.setNutritionalValues(getNutritionalValues(productOld.getNutritionalValues()));
                productDynamo.setData(productDataDynamo);

                log.debug("Migrating product to: " + productDynamo);
                migratedProducts.put(productDynamo.getData().getName().toLowerCase(), productDynamo);
            }
        });
    }

    private void migrateRecipesPrivate() {
        final DynamoDbTable<RecipeDynamo> table = dynamoClient.createTable(RecipeDynamo.TABLE_SCHEMA);
        if (exist(RecipeDynamo.KEY, table)) {
            log.debug("Not possible to migrate recipes because they exist");
            print(RecipeDynamo.KEY, table);
            return;
        }

        log.debug("Migrating recipes");
        final DynamoDbTable<RecipeOldDynamo> oldTable = dynamoClient.createTable(appConfig.dynamo().oldTableName(), RecipeOldDynamo.TABLE_SCHEMA);

        Key key = Key.builder()
                .partitionValue("recipe")
                .build();

        PageIterable<RecipeOldDynamo> results = oldTable.query(r -> r.queryConditional(QueryConditional.keyEqualTo(key)));
        results.stream().forEach(page -> {
            for (RecipeOldDynamo recipeOld : page.items()) {
                log.debug("Migrating from: " + recipeOld);

                final RecipeDynamo recipe = new RecipeDynamo();
                recipe.setId(IdFormat.createId());
                final RecipeDataDynamo recipeData = new RecipeDataDynamo();
                recipeData.setDescription(recipeOld.getDescription());
                recipeData.setName(recipeOld.getName());
                recipeData.setProducts(getProducts(recipeOld.getProducts()));
                recipeData.setNutritionalValues(getNutritionalValues(recipeOld.getNutritionalValues()));
                recipe.setData(recipeData);

                log.debug("Migrating to: " + recipe);
                migratedRecipes.put(recipe.getData().getName().toLowerCase(), recipe);
            }
        });
    }

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
                            final ProductValueDynamo productValueDynamo = new ProductValueDynamo();
                            final String name;
                            if ("Carpaccio de remolacha, zanahoria y mozarella".equals(product.get("name").asText())) {
                                name = "Carpaccio de remolacha, zanahoria y branza".toLowerCase();
                            } else if ("Ensalada de col, rabano y zanahoria".equals(product.get("name").asText())) {
                                name = "Ensalada de col, rabano, zanahoria y queso".toLowerCase();
                            } else {
                                name = product.get("name").asText().toLowerCase();
                            }
                            final ProductDynamo productDynamo = migratedProducts.get(name);
                            if (productDynamo == null) {
                                throw new RuntimeException("Product null: " + product);
                            }
                            productValueDynamo.setId(productDynamo.getId());
                            productValueDynamo.setUnit("g");
                            productValueDynamo.setName(productDynamo.getData().getName());
                            productValueDynamo.setValue(product.get("value").asDouble());
                            productValueDynamo.setRecipeId(productDynamo.getData().getRecipeId());
                            if (StringUtils.hasText(productValueDynamo.getRecipeId())) {
                                productValueDynamo.setUnit(productValueDynamo.getValue() > 1 ? "portions" : "portion");
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

    private List<NutritionalValueDynamo> getNutritionalValues(JsonNode nutritionalValues) {
        final List<NutritionalInformationDynamo> nutritionalInformationDynamos = nutritionalInformationRepository.get();
        Map<String, NutritionalInformationDynamo> nutritionalInfoByShortName = new HashMap<>();
        final ArrayList<NutritionalValueDynamo> nutritionalValueDynamos = new ArrayList<>();
        for (NutritionalInformationDynamo ni : nutritionalInformationDynamos) {
            if (ni.getData() == null || ni.getData().getName() == null)
                throw new RuntimeException("NutritionalValue null: " + ni);
            nutritionalInfoByShortName.put(ni.getData().getName().toLowerCase(), ni);
        }

        for (JsonNode nv : nutritionalValues) {
            final NutritionalValueDynamo nutritionalValueDynamo = new NutritionalValueDynamo();
            final NutritionalInformationDynamo nutritionalInformationDynamo;
            if (nv.has("name")) {
                nutritionalInformationDynamo = nutritionalInfoByShortName.get(nv.get("name").asText().toLowerCase());
                nutritionalValueDynamo.setValue(nv.get("value").asDouble());
            } else {
                nutritionalInformationDynamo = nutritionalInfoByShortName.get(nv.get(0).asText().toLowerCase());
                nutritionalValueDynamo.setValue(nv.get(2).asDouble());
            }
            nutritionalValueDynamo.setId(nutritionalInformationDynamo.getId());
            nutritionalValueDynamo.setUnit(nutritionalInformationDynamo.getData().getUnit());
            nutritionalValueDynamo.setName(nutritionalInformationDynamo.getData().getName());
            nutritionalValueDynamo.setShortName(nutritionalInformationDynamo.getData().getShortName());
            nutritionalValueDynamos.add(nutritionalValueDynamo);
        }

        return nutritionalValueDynamos;
    }

    private List<ProductValueDynamo> getProducts(JsonNode products) {
        final ArrayList<ProductValueDynamo> productValueDynamos = new ArrayList<>();
        for (JsonNode product : products) {
            final ProductValueDynamo productValueDynamo = new ProductValueDynamo();
            productValueDynamo.setName(product.get(0).asText().toLowerCase());
            productValueDynamo.setValue(product.get(2).asDouble());
            productValueDynamos.add(productValueDynamo);
        }

        return productValueDynamos;
    }
}
