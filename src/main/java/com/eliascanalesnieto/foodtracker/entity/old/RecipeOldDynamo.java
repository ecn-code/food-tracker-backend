package com.eliascanalesnieto.foodtracker.entity.old;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Setter;
import lombok.ToString;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbConvertedBy;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

@DynamoDbBean
@ToString
@Setter
public class RecipeOldDynamo {

    public static final TableSchema<RecipeOldDynamo> TABLE_SCHEMA = TableSchema.fromBean(RecipeOldDynamo.class);

    private String pk;
    private String sk;
    private String name;
    private String description;
    private JsonNode products;
    private JsonNode nutritionalValues;

    @DynamoDbPartitionKey
    @DynamoDbAttribute("PK")
    public String getPk() {
        return pk;
    }

    @DynamoDbSortKey
    @DynamoDbAttribute("SK")
    public String getSk() {
        return sk;
    }

    @DynamoDbAttribute("name")
    public String getName() {
        return name;
    }

    @DynamoDbConvertedBy(JsonNodeConverter.class)
    @DynamoDbAttribute("products")
    public JsonNode getProducts() {
        return products;
    }

    @DynamoDbAttribute("description")
    public String getDescription() {
        return description;
    }

    @DynamoDbConvertedBy(JsonNodeConverter.class)
    @DynamoDbAttribute("nutritional_value")
    public JsonNode getNutritionalValues() {
        return nutritionalValues;
    }

}
