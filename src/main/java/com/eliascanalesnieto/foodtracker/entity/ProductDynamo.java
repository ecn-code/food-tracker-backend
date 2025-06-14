package com.eliascanalesnieto.foodtracker.entity;

import lombok.Getter;
import lombok.Setter;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.Key;

@Getter
@Setter
@DynamoDbBean
public class ProductDynamo {

    public static final TableSchema<ProductDynamo> TABLE_SCHEMA = TableSchema.fromBean(ProductDynamo.class);
    public static final Key KEY = Key.builder().partitionValue("PRODUCT_V2").build();

    private String type;
    private String id;
    private ProductDataDynamo data;

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
    public ProductDataDynamo getData() {
        return data;
    }
}
