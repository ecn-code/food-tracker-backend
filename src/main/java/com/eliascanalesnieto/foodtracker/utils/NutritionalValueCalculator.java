package com.eliascanalesnieto.foodtracker.utils;

import com.eliascanalesnieto.foodtracker.dto.in.ProductValueRequest;
import com.eliascanalesnieto.foodtracker.entity.ProductDynamo;
import com.eliascanalesnieto.foodtracker.model.ItemValue;
import lombok.experimental.UtilityClass;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@UtilityClass
public class NutritionalValueCalculator {

    public static Collection<ItemValue> mergeList(List<ProductValueRequest> items, final Function<String, ProductDynamo> getProduct) {
        return merge(items.stream(), getProduct);
    }

    public static Collection<ItemValue> mergeListOfLists(Collection<List<ProductValueRequest>> items, final Function<String, ProductDynamo> getProduct) {
        return merge(items.stream().flatMap(List::stream), getProduct);
    }

    private static Collection<ItemValue> merge(Stream<ProductValueRequest> items, final Function<String, ProductDynamo> getProduct) {
        return items
                .map(itemValueRequest -> NutritionalValueCalculator.applyFactor(
                        getProduct.apply(itemValueRequest.id()),
                        itemValueRequest
                ))
                .flatMap(List::stream)
                .collect(Collectors.toMap(
                        ItemValue::id,
                        iv -> iv,
                        (iv1, iv2) -> new ItemValue(
                                iv1.id(),
                                iv1.name(),
                                iv1.shortName(),
                                iv1.unit(),
                                iv1.value() + iv2.value()
                        )
                )).values();
    }

    private static List<ItemValue> applyFactor(final ProductDynamo productDynamo, final ProductValueRequest productValueRequest) {
        double factor = getValue(productValueRequest.recipeId(), productValueRequest.value()) / 100.0;
        return productDynamo.getData().getNutritionalValues().stream()
                .map(nv -> new ItemValue(
                        nv.getId(), nv.getName(), nv.getShortName(), nv.getUnit(), nv.getQuantity() * factor
                )).toList();
    }

    private static Double getValue(final String recipeId, final Double value) {
        return StringUtils.hasText(recipeId) ? 100 * value : value;
    }
}