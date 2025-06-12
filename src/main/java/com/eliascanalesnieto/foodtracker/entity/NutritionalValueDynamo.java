package com.eliascanalesnieto.foodtracker.entity;

import com.eliascanalesnieto.foodtracker.dto.in.NutritionalValueRequest;
import com.eliascanalesnieto.foodtracker.model.ItemValue;
import lombok.Setter;
import lombok.ToString;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;

@Setter
@ToString
@DynamoDbBean
public class NutritionalValueDynamo {

    private String id;
    private String unit;
    private String name;
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

    public static NutritionalValueDynamo build(final NutritionalValueRequest itemValueRequest) {
        NutritionalValueDynamo d = new NutritionalValueDynamo();
        d.setId(itemValueRequest.id());
        d.setName(itemValueRequest.name());
        d.setUnit(itemValueRequest.unit());
        d.setQuantity(itemValueRequest.value());
        return d;
    }

    public static NutritionalValueDynamo build(final ItemValue itemValue) {
        NutritionalValueDynamo d = new NutritionalValueDynamo();
        d.setId(itemValue.id());
        d.setName(itemValue.name());
        d.setUnit(itemValue.unit());
        d.setQuantity(itemValue.value());
        return d;
    }
}
