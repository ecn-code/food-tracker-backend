package com.eliascanalesnieto.foodtracker.entity;

import lombok.Setter;
import lombok.ToString;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;

@Setter
@ToString
@DynamoDbBean
public class NutritionalInformationDataDynamo {

    private String shortName;
    private String name;
    private String unit;

    @DynamoDbAttribute("short_name")
    public String getShortName() {
        return shortName;
    }

    @DynamoDbAttribute("name")
    public String getName() {
        return name;
    }

    @DynamoDbAttribute("unit")
    public String getUnit() {
        return unit;
    }
}