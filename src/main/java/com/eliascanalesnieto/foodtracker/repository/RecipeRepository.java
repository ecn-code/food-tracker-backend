package com.eliascanalesnieto.foodtracker.repository;

import com.eliascanalesnieto.foodtracker.entity.NutritionalValueDynamo;
import com.eliascanalesnieto.foodtracker.entity.ProductValueDynamo;
import com.eliascanalesnieto.foodtracker.entity.RecipeDataDynamo;
import com.eliascanalesnieto.foodtracker.entity.RecipeDynamo;
import com.eliascanalesnieto.foodtracker.exception.EntityNotFoundException;
import com.eliascanalesnieto.foodtracker.model.Recipe;
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

    public RecipeDynamo create(final Recipe recipe) {
        return replace(recipe,"attribute_not_exists(PK) AND attribute_not_exists(SK)");
    }

    public RecipeDynamo update(final Recipe recipe) {
        return replace(recipe, "attribute_exists(PK) AND attribute_exists(SK)");
    }

    public void delete(String id) {
        dynamoDbTable.deleteItem(RecipeDynamo.KEY.toBuilder().sortValue(id).build());
    }

    private RecipeDynamo replace(Recipe recipe, String expression) {
        final RecipeDynamo recipeDynamo = new RecipeDynamo();
        recipeDynamo.setType(RecipeDynamo.KEY.partitionKeyValue().s());
        recipeDynamo.setId(recipe.id());

        final RecipeDataDynamo recipeDataDynamo = new RecipeDataDynamo();
        recipeDynamo.setData(recipeDataDynamo);
        recipeDataDynamo.setName(recipe.name());
        recipeDataDynamo.setDescription(recipe.description());

        if (recipe.products() != null) {
            recipeDataDynamo.setProducts(
                    recipe.products().stream()
                            .map(ProductValueDynamo::build)
                            .collect(Collectors.toList())
            );
        } else {
            recipeDataDynamo.setProducts(null);
        }

        if (recipe.nutritionalValues() != null) {
            recipeDataDynamo.setNutritionalValues(
                    recipe.nutritionalValues().stream()
                            .map(NutritionalValueDynamo::build)
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
}