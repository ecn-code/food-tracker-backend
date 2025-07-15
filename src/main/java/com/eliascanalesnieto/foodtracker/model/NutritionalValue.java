package com.eliascanalesnieto.foodtracker.model;

import com.eliascanalesnieto.foodtracker.dto.in.NutritionalValueRequest;
import com.eliascanalesnieto.foodtracker.entity.NutritionalInformationDynamo;
import com.eliascanalesnieto.foodtracker.entity.NutritionalValueDynamo;

import java.util.List;

public record NutritionalValue(String id, String shortName, String name, String unit, Double value) {

    public static NutritionalValue build(final NutritionalValueDynamo nutritionalValueDynamo) {
        return new NutritionalValue(
                nutritionalValueDynamo.getId(),
                nutritionalValueDynamo.getShortName(),
                nutritionalValueDynamo.getName(),
                nutritionalValueDynamo.getUnit(),
                nutritionalValueDynamo.getValue()
        );
    }

    public static NutritionalValue applyFactor(final NutritionalValue nutritionalValue, final Double factor) {
        return new NutritionalValue(
                nutritionalValue.id(),
                nutritionalValue.shortName(),
                nutritionalValue.name(),
                nutritionalValue.unit(),
                nutritionalValue.value() * factor
        );
    }

    public static NutritionalValue build(final NutritionalValueRequest nutritionalValueRequest,
                                               final NutritionalInformationDynamo nutritionalInformationDynamo) {
        return new NutritionalValue(
                nutritionalValueRequest.id(),
                nutritionalInformationDynamo.getData().getShortName(),
                nutritionalInformationDynamo.getData().getName(),
                nutritionalInformationDynamo.getData().getUnit(),
                nutritionalValueRequest.value()
        );
    }

    public static List<NutritionalValue> applyFactor(final List<NutritionalValue> nutritionalValues, final Double factor) {
        return nutritionalValues.stream()
                .map(nutritionalValue -> NutritionalValue.applyFactor(nutritionalValue, factor))
                .toList();
    }

    public static NutritionalValue sum(final NutritionalValue item, final NutritionalValue other) {
        return new NutritionalValue(
                item.id(),
                item.shortName(),
                item.name(),
                item.unit(),
                item.value() + other.value()
        );
    }
}
