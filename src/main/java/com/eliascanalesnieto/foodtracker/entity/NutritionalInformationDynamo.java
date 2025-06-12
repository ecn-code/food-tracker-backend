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
public class NutritionalInformationDynamo {

    public static final TableSchema<NutritionalInformationDynamo> TABLE_SCHEMA = TableSchema.fromBean(NutritionalInformationDynamo.class);
    public static final Key KEY = Key.builder().partitionValue("NUTRITIONAL_INFORMATION_V2").build();

    private String type;
    private String id;
    private NutritionalInformationDataDynamo data;

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
    public NutritionalInformationDataDynamo getData() {
        return data;
    }
}
