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
public class SettingDynamo {

    public SettingDynamo() {
        this.type = KEY.partitionKeyValue().s();
    }

    public static final TableSchema<SettingDynamo> TABLE_SCHEMA = TableSchema.fromBean(SettingDynamo.class);
    public static final Key KEY = Key.builder().partitionValue("SETTING_V2").build();

    private String type;
    private String version;
    private SettingDataDynamo data;

    @DynamoDbPartitionKey
    @DynamoDbAttribute("PK")
    public String getType() {
        return type;
    }

    @DynamoDbSortKey
    @DynamoDbAttribute("SK")
    public String getVersion() {
        return version;
    }

    @DynamoDbAttribute("additional_data")
    public SettingDataDynamo getData() {
        return data;
    }
}
