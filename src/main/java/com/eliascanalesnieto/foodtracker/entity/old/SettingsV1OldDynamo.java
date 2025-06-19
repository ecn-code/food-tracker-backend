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
public class SettingsV1OldDynamo {

    public static final TableSchema<SettingsV1OldDynamo> TABLE_SCHEMA = TableSchema.fromBean(SettingsV1OldDynamo.class);

    private String pk;
    private String sk;
    private JsonNode settings;

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

    @DynamoDbConvertedBy(JsonNodeConverter.class)
    @DynamoDbAttribute("settings")
    public JsonNode getSettings() {
        return settings;
    }
}
