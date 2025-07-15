package com.eliascanalesnieto.foodtracker.entity;

import com.eliascanalesnieto.foodtracker.model.NutritionalValue;
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
    private String shortName;
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

    @DynamoDbAttribute("short_name")
    public String getShortName() {
        return shortName;
    }

    public static NutritionalValueDynamo build(final NutritionalValue nutritionalValue) {
        NutritionalValueDynamo d = new NutritionalValueDynamo();
        d.setId(nutritionalValue.id());
        d.setName(nutritionalValue.name());
        d.setUnit(nutritionalValue.unit());
        d.setShortName(nutritionalValue.shortName());
        d.setValue(nutritionalValue.value());
        return d;
    }
}
