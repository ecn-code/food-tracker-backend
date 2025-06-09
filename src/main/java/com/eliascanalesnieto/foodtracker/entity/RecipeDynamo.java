package com.eliascanalesnieto.foodtracker.entity;

import lombok.Setter;
import lombok.ToString;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

import java.util.UUID;

@Setter
@ToString
@DynamoDbBean
public class RecipeDynamo {

    public static final TableSchema<RecipeDynamo> TABLE_SCHEMA = TableSchema.fromBean(RecipeDynamo.class);
    public static final Key KEY = Key.builder().partitionValue("RECIPE_V2").build();

    private String type;
    private String id;
    private RecipeDataDynamo data;

    @DynamoDbPartitionKey
    @DynamoDbAttribute("PK")
    public String getType() {
        return type;
    }

    @DynamoDbSortKey
    @DynamoDbAttribute("SK")
    public String getId() {
        return id;
    }

    @DynamoDbAttribute("additional_data")
    public RecipeDataDynamo getData() {
        return data;
    }

    public static String createId() {
        return UUID.randomUUID().toString();
    }
}
