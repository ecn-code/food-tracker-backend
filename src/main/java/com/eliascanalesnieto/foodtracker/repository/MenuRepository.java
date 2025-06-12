package com.eliascanalesnieto.foodtracker.repository;

import com.eliascanalesnieto.foodtracker.entity.NutritionalValueDynamo;
import com.eliascanalesnieto.foodtracker.entity.MenuDataDynamo;
import com.eliascanalesnieto.foodtracker.entity.MenuDynamo;
import com.eliascanalesnieto.foodtracker.entity.ProductValueDynamo;
import com.eliascanalesnieto.foodtracker.exception.EntityNotFoundException;
import com.eliascanalesnieto.foodtracker.exception.UnprocessableContent;
import com.eliascanalesnieto.foodtracker.model.Menu;
import com.eliascanalesnieto.foodtracker.utils.DateFormat;
import com.eliascanalesnieto.foodtracker.utils.IdFormat;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Expression;
import software.amazon.awssdk.enhanced.dynamodb.model.PutItemEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;

import java.text.ParseException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class MenuRepository {

    private final DynamoDbTable<MenuDynamo> dynamoDbTable;

    public MenuRepository(final DynamoClient dynamoClient) {
        this.dynamoDbTable = dynamoClient.createTable(MenuDynamo.TABLE_SCHEMA);
    }

    public List<MenuDynamo> get() {
        return dynamoDbTable.query(r -> r.queryConditional(QueryConditional.keyEqualTo(MenuDynamo.KEY)))
                .items().stream().toList();
    }

    public MenuDynamo get(final String id) throws EntityNotFoundException {
        return dynamoDbTable.query(r -> r.queryConditional(
                QueryConditional.keyEqualTo(MenuDynamo.KEY.toBuilder().sortValue(id).build()))
        ).items().stream().findFirst().orElseThrow(EntityNotFoundException::new);
    }

    public MenuDynamo create(final Menu menu) throws ParseException {
        return replace(menu);
    }

    public MenuDynamo update(final String id, final Menu menu) throws ParseException, UnprocessableContent {
        if (!id.equals(IdFormat.format(DateFormat.format(menu.date()), menu.username()))) {
            throw new UnprocessableContent();
        }
        return replace(menu,"attribute_exists(PK) AND attribute_exists(SK)", id);
    }

    public void delete(final String id) throws EntityNotFoundException {
        get(id);
        dynamoDbTable.deleteItem(MenuDynamo.KEY.toBuilder().sortValue(id).build());
    }

    private MenuDynamo replace(Menu menu) throws ParseException {
        final String id = IdFormat.format(DateFormat.format(menu.date()), menu.username());
        return replace(menu, "attribute_not_exists(PK) AND attribute_not_exists(SK)", id);
    }

    private MenuDynamo replace(Menu menuRequest, String expression, String id) throws ParseException {
        MenuDynamo menuDynamo = new MenuDynamo();
        menuDynamo.setType(MenuDynamo.KEY.partitionKeyValue().s());
        menuDynamo.setDateUsername(id);

        MenuDataDynamo menuDataDynamo = new MenuDataDynamo();
        menuDynamo.setData(menuDataDynamo);

        if (menuRequest.products() != null) {
            menuDataDynamo.setProducts(
                    menuRequest.products().entrySet().stream()
                            .collect(Collectors.toMap(
                                    Map.Entry::getKey,
                                    e -> e.getValue().stream()
                                            .map(ProductValueDynamo::build)
                                            .collect(Collectors.toList())
                            ))
            );
        } else {
            menuDataDynamo.setProducts(null);
        }

        if (menuRequest.nutritionalValues() != null) {
            menuDataDynamo.setNutritionalValues(
                    menuRequest.nutritionalValues().stream()
                            .map(NutritionalValueDynamo::build)
                            .collect(Collectors.toList())
            );
        } else {
            menuDataDynamo.setNutritionalValues(null);
        }

        PutItemEnhancedRequest<MenuDynamo> request = PutItemEnhancedRequest.builder(MenuDynamo.class)
                .item(menuDynamo)
                .conditionExpression(
                        Expression.builder()
                                .expression(expression)
                                .build()
                ).build();
        dynamoDbTable.putItem(request);

        return menuDynamo;
    }
}
