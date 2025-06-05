package com.eliascanalesnieto.foodtracker.entity;

import lombok.Setter;
import lombok.ToString;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;

@Setter
@ToString
@DynamoDbBean
public class NutritionalValueQuantityDynamoDb {

    private String unit;
    private String name;
    private String quantity;

    @DynamoDbAttribute("name")
    public String getName() {
        return name;
    }

    @DynamoDbAttribute("quantity")
    public String getQuantity() {
        return quantity;
    }

    @DynamoDbAttribute("unit")
    public String getUnit() {
        return unit;
    }
}
