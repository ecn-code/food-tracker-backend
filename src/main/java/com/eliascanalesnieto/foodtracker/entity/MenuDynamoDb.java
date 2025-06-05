package com.eliascanalesnieto.foodtracker.entity;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class MenuDynamoDb {

    private Date date;
    private List<NutritionalValueQuantityDynamoDb> nutritionalValues;
    private Map<String, List<MenuProductDynamoDb>> products;

    @DynamoDbAttribute("date")
    public Date getDate() {
        return date;
    }

    @DynamoDbAttribute("nutritional_values")
    public List<NutritionalValueQuantityDynamoDb> getNutritionalValues() {
        return nutritionalValues;
    }

    @DynamoDbAttribute("products")
    public Map<String, List<MenuProductDynamoDb>> getProducts() {
        return products;
    }
}
