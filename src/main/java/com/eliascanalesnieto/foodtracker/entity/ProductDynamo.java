package com.eliascanalesnieto.foodtracker.entity;

import lombok.Setter;
import lombok.ToString;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondaryPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondarySortKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.Key;

@Setter
@ToString
@DynamoDbBean
public class ProductDynamo {

    public ProductDynamo() {
        type = KEY.partitionKeyValue().s();
    }

    public static final TableSchema<ProductDynamo> TABLE_SCHEMA = TableSchema.fromBean(ProductDynamo.class);
    public static final Key KEY = Key.builder().partitionValue("PRODUCT_V2").build();

    private String type;
    private String id;
    private String orderBy;
    private String findBy;
    private ProductDataDynamo data;

    @DynamoDbPartitionKey
    @DynamoDbAttribute("PK")
    @DynamoDbSecondaryPartitionKey(indexNames = "pk-orderby-index")
    public String getType() {
        return type;
    }

    @DynamoDbSortKey
    @DynamoDbAttribute("SK")
    public String getId() {
        return id;
    }

    @DynamoDbAttribute("order_by")
    @DynamoDbSecondarySortKey(indexNames = "pk-orderby-index")
    public String getOrderBy() {
        return data.getName().toLowerCase();
    }

    @DynamoDbAttribute("find_by")
    public String getFindBy() {
        return data.getFindBy();
    }

    @DynamoDbAttribute("additional_data")
    public ProductDataDynamo getData() {
        return data;
    }
}
