package com.eliascanalesnieto.foodtracker.repository;

import com.eliascanalesnieto.foodtracker.dto.in.UnitRequest;
import com.eliascanalesnieto.foodtracker.entity.UnitDataDynamo;
import com.eliascanalesnieto.foodtracker.entity.UnitDynamo;
import com.eliascanalesnieto.foodtracker.exception.EntityNotFoundException;
import com.eliascanalesnieto.foodtracker.utils.IdFormat;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Expression;
import software.amazon.awssdk.enhanced.dynamodb.model.PutItemEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;

import java.util.List;

@Repository
public class UnitRepository {

    private final DynamoDbTable<UnitDynamo> dynamoDbTable;

    public UnitRepository(final DynamoClient dynamoClient) {
        this.dynamoDbTable = dynamoClient.createTable(UnitDynamo.TABLE_SCHEMA);
    }

    public List<UnitDynamo> get() {
        return dynamoDbTable.query(r -> r.queryConditional(QueryConditional.keyEqualTo(UnitDynamo.KEY)))
                .items().stream().toList();
    }

    public UnitDynamo get(final String id) throws EntityNotFoundException {
        return dynamoDbTable.query(r -> r.queryConditional(
                QueryConditional.keyEqualTo(UnitDynamo.KEY.toBuilder().sortValue(id).build()))
        ).items().stream().findFirst().orElseThrow(EntityNotFoundException::new);
    }

    public UnitDynamo create(final UnitRequest unitRequest) {
        return replace(new UnitRequest(IdFormat.createId(), unitRequest.shortName(), unitRequest.name()),
                "attribute_not_exists(PK) AND attribute_not_exists(SK)");
    }

    public UnitDynamo update(final UnitRequest unitRequest) {
        return replace(unitRequest, "attribute_exists(PK) AND attribute_exists(SK)");
    }

    public void delete(String id) {
        dynamoDbTable.deleteItem(UnitDynamo.KEY.toBuilder().sortValue(id).build());
    }

    private UnitDynamo replace(UnitRequest unitRequest, String expression) {
        final UnitDynamo unitDynamo = new UnitDynamo();
        unitDynamo.setType(UnitDynamo.KEY.partitionKeyValue().s());
        unitDynamo.setId(unitRequest.id());

        final UnitDataDynamo unitDataDynamo = new UnitDataDynamo();
        unitDynamo.setData(unitDataDynamo);
        unitDataDynamo.setName(unitRequest.name());
        unitDataDynamo.setShortName(unitRequest.shortName());

        final PutItemEnhancedRequest<UnitDynamo> request = PutItemEnhancedRequest.builder(UnitDynamo.class)
                .item(unitDynamo)
                .conditionExpression(
                        Expression.builder()
                                .expression(expression)
                                .build()
                )
                .build();
        dynamoDbTable.putItem(request);

        return unitDynamo;
    }
}
