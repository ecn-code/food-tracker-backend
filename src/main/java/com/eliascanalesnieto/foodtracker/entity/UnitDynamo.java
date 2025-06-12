package com.eliascanalesnieto.foodtracker.entity;

import lombok.Setter;
import lombok.ToString;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

@Setter
@ToString
@DynamoDbBean
public class UnitDynamo {

    public static final TableSchema<UnitDynamo> TABLE_SCHEMA = TableSchema.fromBean(UnitDynamo.class);
    public static final Key KEY = Key.builder().partitionValue("UNIT_V2").build();

    private String type;
    private String id;
    private UnitDataDynamo data;

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
    public UnitDataDynamo getData() {
        return data;
    }
}
