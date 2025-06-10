package com.eliascanalesnieto.foodtracker.utils;

import com.eliascanalesnieto.foodtracker.entity.ItemValueDynamo;
import com.eliascanalesnieto.foodtracker.model.ItemValue;
import lombok.experimental.UtilityClass;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@UtilityClass
public class NutritionalValueCalculator {

    public static List<ItemValue> merge(List<ItemValueDynamo> items) {
        if (items == null) return List.of();

        Map<String, ItemValueDynamo> merged = items.stream()
                .collect(Collectors.toMap(
                        ItemValueDynamo::getId,
                        iv -> iv,
                        (iv1, iv2) -> {
                            iv1.setQuantity(iv1.getQuantity() + iv2.getQuantity());
                            return iv1;
                        }
                ));

        return merged.values().stream()
                .map(iv -> new ItemValue(
                        iv.getId(),
                        iv.getName(),
                        iv.getUnit(),
                        iv.getQuantity()
                ))
                .collect(Collectors.toList());
    }

}