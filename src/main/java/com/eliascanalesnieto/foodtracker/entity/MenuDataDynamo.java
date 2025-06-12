package com.eliascanalesnieto.foodtracker.entity;

import lombok.Getter;
import lombok.Setter;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@DynamoDbBean
public class MenuDataDynamo {

    private List<NutritionalValueDynamo> nutritionalValues;
    private Map<String, List<ProductValueDynamo>> products;

    @DynamoDbAttribute("nutritional_values")
    public List<NutritionalValueDynamo> getNutritionalValues() {
        return nutritionalValues;
    }

    @DynamoDbAttribute("products")
    public Map<String, List<ProductValueDynamo>> getProducts() {
        return products;
    }

}
