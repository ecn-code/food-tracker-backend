package com.eliascanalesnieto.foodtracker.repository;

import com.eliascanalesnieto.foodtracker.dto.in.NutritionalInformationRequest;
import com.eliascanalesnieto.foodtracker.entity.NutritionalInformationDataDynamo;
import com.eliascanalesnieto.foodtracker.entity.NutritionalInformationDynamo;
import com.eliascanalesnieto.foodtracker.exception.EntityNotFoundException;
import com.eliascanalesnieto.foodtracker.utils.IdFormat;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Expression;
import software.amazon.awssdk.enhanced.dynamodb.model.PutItemEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;

import java.util.List;

@Repository
public class NutritionalInformationRepository {

    private final DynamoDbTable<NutritionalInformationDynamo> dynamoDbTable;

    public NutritionalInformationRepository(final DynamoClient dynamoClient) {
        this.dynamoDbTable = dynamoClient.createTable(NutritionalInformationDynamo.TABLE_SCHEMA);
    }

    public List<NutritionalInformationDynamo> get() {
        return dynamoDbTable.query(r -> r.queryConditional(QueryConditional.keyEqualTo(NutritionalInformationDynamo.KEY)))
                .items().stream().toList();
    }

    public NutritionalInformationDynamo get(final String id) throws EntityNotFoundException {
        return dynamoDbTable.query(r -> r.queryConditional(
                QueryConditional.keyEqualTo(NutritionalInformationDynamo.KEY.toBuilder().sortValue(id).build()))
        ).items().stream().findFirst().orElseThrow(EntityNotFoundException::new);
    }

    public NutritionalInformationDynamo create(final NutritionalInformationRequest request) {
        return replace(new NutritionalInformationRequest(
                        IdFormat.createId(),
                        request.shortName(),
                        request.name(),
                        request.unit()
                ),
                "attribute_not_exists(PK) AND attribute_not_exists(SK)");
    }

    public NutritionalInformationDynamo update(final NutritionalInformationRequest request) {
        return replace(request, "attribute_exists(PK) AND attribute_exists(SK)");
    }

    public void delete(String id) {
        dynamoDbTable.deleteItem(NutritionalInformationDynamo.KEY.toBuilder().sortValue(id).build());
    }

    private NutritionalInformationDynamo replace(NutritionalInformationRequest request, String expression) {
        final NutritionalInformationDynamo niDynamo = new NutritionalInformationDynamo();
        niDynamo.setType(NutritionalInformationDynamo.KEY.partitionKeyValue().s());
        niDynamo.setId(request.id());

        final NutritionalInformationDataDynamo data = new NutritionalInformationDataDynamo();
        data.setName(request.name());
        data.setShortName(request.shortName());
        data.setUnit(request.unit());
        niDynamo.setData(data);

        final PutItemEnhancedRequest<NutritionalInformationDynamo> putRequest = PutItemEnhancedRequest.builder(NutritionalInformationDynamo.class)
                .item(niDynamo)
                .conditionExpression(
                        Expression.builder()
                                .expression(expression)
                                .build()
                )
                .build();
        dynamoDbTable.putItem(putRequest);

        return niDynamo;
    }
}
