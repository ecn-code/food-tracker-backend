package com.eliascanalesnieto.foodtracker.entity;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class MenuDynamo {

    private Date date;
    private List<NutritionalValueQuantityDynamo> nutritionalValues;
    private Map<String, List<MenuProductDynamo>> products;

    @DynamoDbAttribute("date")
    public Date getDate() {
        return date;
    }

    @DynamoDbAttribute("nutritional_values")
    public List<NutritionalValueQuantityDynamo> getNutritionalValues() {
        return nutritionalValues;
    }

    @DynamoDbAttribute("products")
    public Map<String, List<MenuProductDynamo>> getProducts() {
        return products;
    }
}
