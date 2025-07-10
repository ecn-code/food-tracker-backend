package com.eliascanalesnieto.foodtracker.utils;

import com.eliascanalesnieto.foodtracker.model.ItemValue;
import com.eliascanalesnieto.foodtracker.model.NutritionalValue;
import lombok.experimental.UtilityClass;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@UtilityClass
public class NutritionalValueCalculator {

    public static Collection<NutritionalValue> mergeList(List<ItemValue> products) {
        return merge(products.stream());
    }

    public static Collection<NutritionalValue> mergeListOfLists(Collection<List<ItemValue>> products) {
        return merge(products.stream().flatMap(List::stream));
    }

    private static Collection<NutritionalValue> merge(Stream<ItemValue> products) {
        return products
                .map(NutritionalValueCalculator::applyFactor)
                .map(ItemValue::nutritionalValueList)
                .flatMap(Collection::stream)
                .collect(Collectors.toMap(
                        NutritionalValue::id,
                        nutritionalValue -> nutritionalValue,
                        NutritionalValue::sum
                ))
                .values();
    }

    private static ItemValue applyFactor(final ItemValue itemValue) {
        double factor = getValue(itemValue.isRecipe(), itemValue.value()) / 100.0;
        return ItemValue.recalculateNutritionalValue(itemValue, factor);
    }

    private static Double getValue(final boolean isRecipe, final Double value) {
        return isRecipe ? 100 * value : value;
    }
}