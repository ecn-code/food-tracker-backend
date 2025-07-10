package com.eliascanalesnieto.foodtracker.entity;

import com.eliascanalesnieto.foodtracker.model.ItemValue;
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
    private Double quantity;

    @DynamoDbAttribute("id")
    public String getId() {
        return id;
    }

    @DynamoDbAttribute("name")
    public String getName() {
        return name;
    }

    @DynamoDbAttribute("quantity")
    public Double getQuantity() {
        return quantity;
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

    public static ProductValueDynamo build(final ItemValue itemValue) {
        ProductValueDynamo d = new ProductValueDynamo();
        d.setId(itemValue.id());
        d.setName(itemValue.name());
        d.setDescription(itemValue.description());
        d.setRecipeId(itemValue.recipeId());
        d.setUnit(itemValue.unit());
        d.setQuantity(itemValue.value());
        return d;
    }
}
