package com.eliascanalesnieto.foodtracker.entity;

import lombok.Getter;
import lombok.Setter;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;

import java.util.List;

@Getter
@Setter
@DynamoDbBean
public class ProductDataDynamo {
    private String name;
    private String description;
    private String recipeId;
    private List<ItemValueDynamo> nutritionalValues;

    @DynamoDbAttribute("name")
    public String getName() {
        return name;
    }

    @DynamoDbAttribute("description")
    public String getDescription() {
        return description;
    }

    @DynamoDbAttribute("recipe_id")
    public String getRecipeId() {
        return recipeId;
    }

    @DynamoDbAttribute("nutritional_values")
    public List<ItemValueDynamo> getNutritionalValues() {
        return nutritionalValues;
    }
}
