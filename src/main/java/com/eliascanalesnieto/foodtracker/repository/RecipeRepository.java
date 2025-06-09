package com.eliascanalesnieto.foodtracker.repository;

import com.eliascanalesnieto.foodtracker.dto.in.ItemValueRequest;
import com.eliascanalesnieto.foodtracker.dto.in.RecipeRequest;
import com.eliascanalesnieto.foodtracker.entity.ItemValueDynamo;
import com.eliascanalesnieto.foodtracker.entity.RecipeDataDynamo;
import com.eliascanalesnieto.foodtracker.entity.RecipeDynamo;
import com.eliascanalesnieto.foodtracker.exception.EntityNotFoundException;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Expression;
import software.amazon.awssdk.enhanced.dynamodb.model.PutItemEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public class RecipeRepository {

    private final DynamoDbTable<RecipeDynamo> dynamoDbTable;

    public RecipeRepository(final DynamoClient dynamoClient) {
        this.dynamoDbTable = dynamoClient.createTable(RecipeDynamo.TABLE_SCHEMA);
    }

    public List<RecipeDynamo> get() {
        return dynamoDbTable.query(r -> r.queryConditional(QueryConditional.keyEqualTo(RecipeDynamo.KEY)))
                .items().stream().toList();
    }

    public RecipeDynamo get(final String id) throws EntityNotFoundException {
        return dynamoDbTable.query(r -> r.queryConditional(
                QueryConditional.keyEqualTo(RecipeDynamo.KEY.toBuilder().sortValue(id).build()))
        ).items().stream().findFirst().orElseThrow(EntityNotFoundException::new);
    }

    public RecipeDynamo create(final RecipeRequest recipeRequest) {
        return replace(new RecipeRequest(RecipeDynamo.createId(), recipeRequest.name(),
                        recipeRequest.description(), recipeRequest.products(), recipeRequest.nutritionalValues()),
                "attribute_not_exists(PK) AND attribute_not_exists(SK)");
    }

    public RecipeDynamo update(final RecipeRequest recipeRequest) {
        return replace(recipeRequest, "attribute_exists(PK) AND attribute_exists(SK)");
    }

    public void delete(String id) {
        dynamoDbTable.deleteItem(RecipeDynamo.KEY.toBuilder().sortValue(id).build());
    }

    private RecipeDynamo replace(RecipeRequest recipeRequest, String expression) {
        final RecipeDynamo recipeDynamo = new RecipeDynamo();
        recipeDynamo.setType(RecipeDynamo.KEY.partitionKeyValue().s());
        recipeDynamo.setId(recipeRequest.id());

        final RecipeDataDynamo recipeDataDynamo = new RecipeDataDynamo();
        recipeDynamo.setData(recipeDataDynamo);
        recipeDataDynamo.setName(recipeRequest.name());
        recipeDataDynamo.setDescription(recipeRequest.description());

        // Conversión de productos
        if (recipeRequest.products() != null) {
            recipeDataDynamo.setProducts(
                recipeRequest.products().stream()
                    .map(this::toItemValueDynamo)
                    .collect(Collectors.toList())
            );
        } else {
            recipeDataDynamo.setProducts(null);
        }

        // Conversión de valores nutricionales
        if (recipeRequest.nutritionalValues() != null) {
            recipeDataDynamo.setNutritionalValues(
                recipeRequest.nutritionalValues().stream()
                    .map(this::toItemValueDynamo)
                    .collect(Collectors.toList())
            );
        } else {
            recipeDataDynamo.setNutritionalValues(null);
        }

        final PutItemEnhancedRequest<RecipeDynamo> request = PutItemEnhancedRequest.builder(RecipeDynamo.class)
                .item(recipeDynamo)
                .conditionExpression(
                        Expression.builder()
                                .expression(expression)
                                .build()
                )
                .build();
        dynamoDbTable.putItem(request);

        return recipeDynamo;
    }

    private ItemValueDynamo toItemValueDynamo(ItemValueRequest req) {
        ItemValueDynamo d = new ItemValueDynamo();
        d.setName(req.name());
        d.setUnit(req.unit());
        d.setQuantity(req.value());
        return d;
    }
}