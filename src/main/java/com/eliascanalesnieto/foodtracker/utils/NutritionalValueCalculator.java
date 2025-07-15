package com.eliascanalesnieto.foodtracker.utils;

import com.eliascanalesnieto.foodtracker.model.ProductValue;
import com.eliascanalesnieto.foodtracker.model.NutritionalValue;
import lombok.experimental.UtilityClass;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@UtilityClass
public class NutritionalValueCalculator {

    public static Collection<NutritionalValue> mergeList(List<ProductValue> products) {
        return merge(products.stream());
    }

    public static Collection<NutritionalValue> mergeListOfLists(Collection<List<ProductValue>> products) {
        return merge(products.stream().flatMap(List::stream));
    }

    private static Collection<NutritionalValue> merge(Stream<ProductValue> products) {
        return products
                .map(NutritionalValueCalculator::applyFactor)
                .map(ProductValue::nutritionalValues)
                .flatMap(Collection::stream)
                .collect(Collectors.toMap(
                        NutritionalValue::id,
                        nutritionalValue -> nutritionalValue,
                        NutritionalValue::sum
                ))
                .values();
    }

    private static ProductValue applyFactor(final ProductValue productValue) {
        double factor = getValue(productValue.isRecipe(), productValue.value()) / 100.0;
        return ProductValue.recalculateNutritionalValue(productValue, factor);
    }

    private static Double getValue(final boolean isRecipe, final Double value) {
        return isRecipe ? 100 * value : value;
    }
}