package com.eliascanalesnieto.foodtracker.repository;

import com.eliascanalesnieto.foodtracker.dto.in.ProductRequest;
import com.eliascanalesnieto.foodtracker.entity.NutritionalValueDynamo;
import com.eliascanalesnieto.foodtracker.entity.ProductDataDynamo;
import com.eliascanalesnieto.foodtracker.entity.ProductDynamo;
import com.eliascanalesnieto.foodtracker.exception.EntityNotFoundException;
import com.eliascanalesnieto.foodtracker.utils.IdFormat;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Expression;
import software.amazon.awssdk.enhanced.dynamodb.model.PutItemEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class ProductRepository {

    private final DynamoDbTable<ProductDynamo> dynamoDbTable;

    public ProductRepository(final DynamoClient dynamoClient) {
        this.dynamoDbTable = dynamoClient.createTable(ProductDynamo.TABLE_SCHEMA);
    }

    public List<ProductDynamo> get() {
        return dynamoDbTable.query(r -> r.queryConditional(QueryConditional.keyEqualTo(ProductDynamo.KEY)))
                .items().stream().toList();
    }

    public ProductDynamo get(final String id) throws EntityNotFoundException {
        return dynamoDbTable.query(r -> r.queryConditional(
                QueryConditional.keyEqualTo(ProductDynamo.KEY.toBuilder().sortValue(id).build()))
        ).items().stream().findFirst().orElseThrow(EntityNotFoundException::new);
    }

    public List<ProductDynamo> get(final List<String> ids) throws EntityNotFoundException {
        final List<ProductDynamo> result = new ArrayList<>();

        for (final String id : ids) {
            result.add(get(id));
        }

        return result;
    }

    public ProductDynamo create(final ProductRequest productRequest) {
        return replace(new ProductRequest(IdFormat.createId(), productRequest.name(),
                        productRequest.description(), productRequest.recipeId(), productRequest.nutritionalValues()),
                "attribute_not_exists(PK) AND attribute_not_exists(SK)");
    }

    public ProductDynamo update(final ProductRequest productRequest) {
        return replace(productRequest, "attribute_exists(PK) AND attribute_exists(SK)");
    }

    public void delete(String id) {
        dynamoDbTable.deleteItem(ProductDynamo.KEY.toBuilder().sortValue(id).build());
    }

    private ProductDynamo replace(ProductRequest productRequest, String expression) {
        final ProductDynamo productDynamo = new ProductDynamo();
        productDynamo.setType(ProductDynamo.KEY.partitionKeyValue().s());
        productDynamo.setId(productRequest.id());

        final ProductDataDynamo productDataDynamo = new ProductDataDynamo();
        productDynamo.setData(productDataDynamo);
        productDataDynamo.setName(productRequest.name());
        productDataDynamo.setDescription(productRequest.description());
        productDataDynamo.setRecipeId(productRequest.recipeId());

        if (productRequest.nutritionalValues() != null) {
            productDataDynamo.setNutritionalValues(
                productRequest.nutritionalValues().stream()
                    .map(NutritionalValueDynamo::build)
                    .collect(Collectors.toList())
            );
        } else {
            productDataDynamo.setNutritionalValues(null);
        }

        final PutItemEnhancedRequest<ProductDynamo> request = PutItemEnhancedRequest.builder(ProductDynamo.class)
                .item(productDynamo)
                .conditionExpression(
                        Expression.builder()
                                .expression(expression)
                                .build()
                )
                .build();
        dynamoDbTable.putItem(request);

        return productDynamo;
    }
}
