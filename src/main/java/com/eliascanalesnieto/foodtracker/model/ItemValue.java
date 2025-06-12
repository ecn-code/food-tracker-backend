package com.eliascanalesnieto.foodtracker.model;

import com.eliascanalesnieto.foodtracker.dto.in.ProductValueRequest;

import java.util.List;
import java.util.stream.Collectors;

public record ItemValue(String id, String name, String unit, Double value) {

    public static List<ItemValue> buildFromProductValues(List<ProductValueRequest> items) {
        return items.stream()
                .map(item -> new ItemValue(
                        item.id(),
                        item.name(),
                        item.unit(),
                        item.value()
                )).collect(Collectors.toList());
    }
}