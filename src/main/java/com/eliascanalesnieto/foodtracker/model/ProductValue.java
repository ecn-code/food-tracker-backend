package com.eliascanalesnieto.foodtracker.model;

import com.eliascanalesnieto.foodtracker.entity.ProductDynamo;
import org.springframework.util.StringUtils;

import java.util.List;

public record ProductValue(String id, String name, String description, String unit, Double value, String recipeId,
                           List<NutritionalValue> nutritionalValues) {

    public boolean isRecipe() {
        return StringUtils.hasText(recipeId);
    }

    public static ProductValue buildFromProductValue(final ProductDynamo item, final Double value) {
        return new ProductValue(
                item.getId(),
                item.getData().getName(),
                item.getData().getDescription(),
                StringUtils.hasText(item.getData().getRecipeId()) ? (value >= 0 ? "portions" : "portion") : "g",
                value,
                item.getData().getRecipeId(),
                item.getData().getNutritionalValues().stream().map(NutritionalValue::build).toList()
        );
    }

    public static ProductValue recalculateNutritionalValue(final ProductValue item, final Double factor) {
        return new ProductValue(
                item.id(),
                item.name(),
                item.description(),
                item.unit(),
                item.value(),
                item.recipeId(),
                NutritionalValue.applyFactor(item.nutritionalValues(), factor)
        );
    }
}