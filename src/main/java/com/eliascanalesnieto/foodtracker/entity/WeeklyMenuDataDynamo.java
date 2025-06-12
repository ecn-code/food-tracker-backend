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

    private List<NutritionalValueDynamo> nutritionalValues;
    private Map<String, MenuDynamo> menus;

    @DynamoDbAttribute("menus")
    public Map<String, MenuDynamo> getMenus() {
        return menus;
    }

    @DynamoDbAttribute("nutritional_values")
    public List<NutritionalValueDynamo> getNutritionalValues() {
        return nutritionalValues;
    }
}
