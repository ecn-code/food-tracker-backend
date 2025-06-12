package com.eliascanalesnieto.foodtracker.model;

import com.eliascanalesnieto.foodtracker.dto.in.MenuRequest;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public record Menu(Date date, String username, Map<String, List<ItemValue>> products,
                   Collection<ItemValue> nutritionalValues) {

    public static Menu build(final MenuRequest menuRequest, final Collection<ItemValue> nutritionalValues) {
        return new Menu(menuRequest.date(), menuRequest.username(),
                menuRequest.products()
                        .entrySet()
                        .stream()
                        .collect(Collectors.toMap(
                                Map.Entry::getKey,
                                entry -> ItemValue.buildFromProductValues(entry.getValue())
                        )), nutritionalValues);
    }
}
