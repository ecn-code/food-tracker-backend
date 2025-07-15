package com.eliascanalesnieto.foodtracker.entity;

import com.eliascanalesnieto.foodtracker.model.ProductValue;
import lombok.Setter;
import lombok.ToString;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;

@Setter
@ToString
@DynamoDbBean
public class ProductValueDynamo {

    private String id;
    private String unit;
    private String name;
    private String description;
    private String recipeId;
    private Double value;

    @DynamoDbAttribute("id")
    public String getId() {
        return id;
    }

    @DynamoDbAttribute("name")
    public String getName() {
        return name;
    }

    @DynamoDbAttribute("value")
    public Double getValue() {
        return value;
    }

    @DynamoDbAttribute("unit")
    public String getUnit() {
        return unit;
    }

    @DynamoDbAttribute("recipe_id")
    public String getRecipeId() {
        return recipeId;
    }

    @DynamoDbAttribute("description")
    public String getDescription() {
        return description;
    }

    public static ProductValueDynamo build(final ProductValue productValue) {
        ProductValueDynamo d = new ProductValueDynamo();
        d.setId(productValue.id());
        d.setName(productValue.name());
        d.setDescription(productValue.description());
        d.setRecipeId(productValue.recipeId());
        d.setUnit(productValue.unit());
        d.setValue(productValue.value());
        return d;
    }
}
