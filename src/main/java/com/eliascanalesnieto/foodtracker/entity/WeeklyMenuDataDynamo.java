package com.eliascanalesnieto.foodtracker.entity;

import lombok.Setter;
import lombok.ToString;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;

import java.util.List;
import java.util.Map;

@Setter
@ToString
@DynamoDbBean
public class WeeklyMenuDataDynamo {

    private List<NutritionalValueQuantityDynamoDb> nutritionalValues;
    private Map<String, MenuDynamoDb> menus;

    @DynamoDbAttribute("menus")
    public Map<String, MenuDynamoDb> getMenus() {
        return menus;
    }

    @DynamoDbAttribute("nutritional_values")
    public List<NutritionalValueQuantityDynamoDb> getNutritionalValues() {
        return nutritionalValues;
    }
}
