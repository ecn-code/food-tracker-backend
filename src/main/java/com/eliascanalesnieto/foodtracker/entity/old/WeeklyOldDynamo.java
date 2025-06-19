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
public class WeeklyOldDynamo {

    public static final TableSchema<WeeklyOldDynamo> TABLE_SCHEMA = TableSchema.fromBean(WeeklyOldDynamo.class);

    private String pk;
    private String sk;
    private String weeklyNumber;
    private String username;
    private JsonNode menus;
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

    @DynamoDbAttribute("username")
    public String getUsername() {
        return username;
    }


    @DynamoDbAttribute("weekly_number")
    public String getWeeklyNumber() {
        return weeklyNumber;
    }

    @DynamoDbConvertedBy(JsonNodeConverter.class)
    @DynamoDbAttribute("menus")
    public JsonNode getMenus() {
        return menus;
    }

    @DynamoDbConvertedBy(JsonNodeConverter.class)
    @DynamoDbAttribute("nutritional_value")
    public JsonNode getNutritionalValues() {
        return nutritionalValues;
    }

}
