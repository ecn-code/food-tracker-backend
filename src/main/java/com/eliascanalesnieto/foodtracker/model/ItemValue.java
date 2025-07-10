package com.eliascanalesnieto.foodtracker.model;

import com.eliascanalesnieto.foodtracker.entity.ProductDynamo;
import org.springframework.util.StringUtils;

import java.util.List;

public record ItemValue(String id, String name, String shortName, String description, String unit, Double value, String recipeId,
                        List<NutritionalValue> nutritionalValueList) {

    public boolean isRecipe() {
        return StringUtils.hasText(recipeId);
    }

    public static ItemValue buildFromProductValue(final ProductDynamo item, final Double value) {
        return new ItemValue(
                item.getId(),
                item.getData().getName(),
                null,
                item.getData().getDescription(),
                StringUtils.hasText(item.getData().getRecipeId()) ? (value >= 0 ? "portions" : "portion") : "g",
                value,
                item.getData().getRecipeId(),
                item.getData().getNutritionalValues().stream().map(NutritionalValue::buildFromNutritionalValue).toList()
        );
    }

    public static ItemValue recalculateNutritionalValue(final ItemValue item, final Double factor) {
        return new ItemValue(
                item.id(),
                item.name(),
                item.shortName(),
                item.description(),
                item.unit(),
                item.value(),
                item.recipeId(),
                NutritionalValue.applyFactor(item.nutritionalValueList(), factor)
        );
    }
}