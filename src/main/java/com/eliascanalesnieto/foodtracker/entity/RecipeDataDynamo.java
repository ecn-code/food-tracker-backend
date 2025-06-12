package com.eliascanalesnieto.foodtracker.entity;

import lombok.Setter;
import lombok.ToString;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;

import java.util.List;

@Setter
@ToString
@DynamoDbBean
public class RecipeDataDynamo {

    private String name;
    private String description;
    private List<ProductValueDynamo> products;
    private List<NutritionalValueDynamo> nutritionalValues;

    @DynamoDbAttribute("name")
    public String getName() {
        return name;
    }

    @DynamoDbAttribute("description")
    public String getDescription() {
        return description;
    }

    @DynamoDbAttribute("products")
    public List<ProductValueDynamo> getProducts() {
        return products;
    }

    @DynamoDbAttribute("nutritional_values")
    public List<NutritionalValueDynamo> getNutritionalValues() {
        return nutritionalValues;
    }
}
