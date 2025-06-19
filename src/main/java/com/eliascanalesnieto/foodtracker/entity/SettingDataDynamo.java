package com.eliascanalesnieto.foodtracker.entity;

import lombok.Setter;
import lombok.ToString;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;

import java.util.List;

@Setter
@ToString
@DynamoDbBean
public class SettingDataDynamo {

    private List<String> partsOfDay;

    @DynamoDbAttribute("parts_of_day")
    public List<String> getPartsOfDay() {
        return partsOfDay;
    }
}
