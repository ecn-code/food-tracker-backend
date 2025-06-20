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
public class ProductOldDynamo {

    public static final TableSchema<ProductOldDynamo> TABLE_SCHEMA = TableSchema.fromBean(ProductOldDynamo.class);

    private String pk;
    private String sk;
    private String name;
    private String description;
    private String recipeName;
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

    @DynamoDbAttribute("recipe_name")
    public String getRecipeName() {
        return recipeName;
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
