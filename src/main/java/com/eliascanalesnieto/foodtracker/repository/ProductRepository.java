package com.eliascanalesnieto.foodtracker.repository;

import com.eliascanalesnieto.foodtracker.entity.NutritionalValueDynamo;
import com.eliascanalesnieto.foodtracker.entity.ProductDataDynamo;
import com.eliascanalesnieto.foodtracker.entity.ProductDynamo;
import com.eliascanalesnieto.foodtracker.exception.EntityNotFoundException;
import com.eliascanalesnieto.foodtracker.model.Paginated;
import com.eliascanalesnieto.foodtracker.model.Product;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;
import software.amazon.awssdk.core.pagination.sync.SdkIterable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbIndex;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Expression;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;
import software.amazon.awssdk.enhanced.dynamodb.model.PutItemEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Repository
public class ProductRepository {

    private final DynamoDbTable<ProductDynamo> dynamoDbTable;

    public ProductRepository(final DynamoClient dynamoClient) {
        this.dynamoDbTable = dynamoClient.createTable(ProductDynamo.TABLE_SCHEMA);
    }

    public Paginated<ProductDynamo> getSortByOrderBy(
            final Integer limit, final Map<String, AttributeValue> lastEvaluatedKey, final String query) {
        DynamoDbIndex<ProductDynamo> orderByIndex = dynamoDbTable.index("pk-orderby-index");

        SdkIterable<Page<ProductDynamo>> pageIterable = orderByIndex.query(getQueryRequest(limit, lastEvaluatedKey, query)
        );

        final Page<ProductDynamo> page = pageIterable.stream().findFirst().orElse(null);
        if (Objects.isNull(page)) {
            return new Paginated<>(Collections.emptyList(), null);
        }

        return new Paginated<>(page.items(), getLastEvaluatedKey(page));
    }

    public Paginated<ProductDynamo> get(final Integer limit, final Map<String, AttributeValue> lastEvaluatedKey) {
        final PageIterable<ProductDynamo> pageIterable = dynamoDbTable.query(
                r -> r.limit(limit)
                        .exclusiveStartKey(lastEvaluatedKey)
                        .queryConditional(QueryConditional.keyEqualTo(ProductDynamo.KEY))
        );

        final Page<ProductDynamo> page = pageIterable.stream().findFirst().orElse(null);
        if (Objects.isNull(page)) {
            return new Paginated<>(Collections.emptyList(), null);
        }

        return new Paginated<>(page.items(), getLastEvaluatedKey(page));
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

    public ProductDynamo create(final Product product) {
        return replace(product, "attribute_not_exists(PK) AND attribute_not_exists(SK)");
    }

    public ProductDynamo update(final Product product) {
        return replace(product, "attribute_exists(PK) AND attribute_exists(SK)");
    }

    public void delete(String id) {
        dynamoDbTable.deleteItem(ProductDynamo.KEY.toBuilder().sortValue(id).build());
    }

    private ProductDynamo replace(Product product, String expression) {
        final ProductDynamo productDynamo = new ProductDynamo();
        productDynamo.setType(ProductDynamo.KEY.partitionKeyValue().s());
        productDynamo.setId(product.id());

        final ProductDataDynamo productDataDynamo = new ProductDataDynamo();
        productDynamo.setData(productDataDynamo);
        productDataDynamo.setName(product.name());
        productDataDynamo.setDescription(product.description());
        productDataDynamo.setRecipeId(product.recipeId());

        if (product.nutritionalValues() != null) {
            productDataDynamo.setNutritionalValues(
                    product.nutritionalValues().stream()
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

    private Map<String, AttributeValue> getLastEvaluatedKey(final Page<ProductDynamo> page) {
        if (Objects.nonNull(page.lastEvaluatedKey()) && !page.lastEvaluatedKey().isEmpty()) {
            return page.lastEvaluatedKey();
        }

        return null;
    }

    private Consumer<QueryEnhancedRequest.Builder> getQueryRequest(
            final Integer limit, final Map<String, AttributeValue> lastEvaluatedKey, final String query) {
        return r -> {
            r.limit(limit)
                    .exclusiveStartKey(lastEvaluatedKey)
                    .scanIndexForward(true)
                    .queryConditional(QueryConditional.keyEqualTo(k -> k.partitionValue(ProductDynamo.KEY.partitionKeyValue())));

            if(StringUtils.hasText(query) && 3 <= query.length()) {
                r.filterExpression(Expression.builder()
                        .expression("contains(find_by, :find_by)")
                        .putExpressionValue(":find_by", AttributeValue.fromS(query.toLowerCase()))
                        .build()
                ).limit(null);
            }
        };
    }
}
