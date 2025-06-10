package com.eliascanalesnieto.foodtracker.repository;

import com.eliascanalesnieto.foodtracker.dto.in.ItemValueRequest;
import com.eliascanalesnieto.foodtracker.dto.in.MenuRequest;
import com.eliascanalesnieto.foodtracker.entity.ItemValueDynamo;
import com.eliascanalesnieto.foodtracker.entity.MenuDataDynamo;
import com.eliascanalesnieto.foodtracker.entity.MenuDynamo;
import com.eliascanalesnieto.foodtracker.exception.EntityNotFoundException;
import com.eliascanalesnieto.foodtracker.exception.UnprocessableContent;
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

    public MenuDynamo create(final MenuRequest menuRequest) throws ParseException {
        return replace(menuRequest);
    }

    public MenuDynamo update(final String id, final MenuRequest menuRequest) throws ParseException, UnprocessableContent {
        if (!id.equals(IdFormat.format(DateFormat.format(menuRequest.date()), menuRequest.username()))) {
            throw new UnprocessableContent();
        }
        return replace(menuRequest,
                "attribute_exists(PK) AND attribute_exists(SK)", id);
    }

    public void delete(final String id) throws EntityNotFoundException {
        get(id);
        dynamoDbTable.deleteItem(MenuDynamo.KEY.toBuilder().sortValue(id).build());
    }

    private MenuDynamo replace(MenuRequest menuRequest) throws ParseException {
        final String id = IdFormat.format(DateFormat.format(menuRequest.date()), menuRequest.username());
        return replace(menuRequest, "attribute_not_exists(PK) AND attribute_not_exists(SK)", id);
    }

    private MenuDynamo replace(MenuRequest menuRequest, String expression, String id) throws ParseException {
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
                                            .map(this::toItemValueDynamo)
                                            .collect(Collectors.toList())
                            ))
            );
        } else {
            menuDataDynamo.setProducts(null);
        }

        if (menuRequest.nutritionalValues() != null) {
            menuDataDynamo.setNutritionalValues(
                    menuRequest.nutritionalValues().stream()
                            .map(this::toItemValueDynamo)
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

    private ItemValueDynamo toItemValueDynamo(ItemValueRequest req) {
        ItemValueDynamo d = new ItemValueDynamo();
        d.setName(req.name());
        d.setUnit(req.unit());
        d.setQuantity(req.value());
        return d;
    }
}
