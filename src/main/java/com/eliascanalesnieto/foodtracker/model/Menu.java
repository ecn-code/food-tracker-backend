package com.eliascanalesnieto.foodtracker.model;

import com.eliascanalesnieto.foodtracker.dto.in.MenuRequest;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

public record Menu(Date date, String username, Map<String, List<ItemValue>> products,
                   Collection<NutritionalValue> nutritionalValues) {

    public static Menu build(final MenuRequest menuRequest, final Map<String, List<ItemValue>> menus,
                             final Collection<NutritionalValue> nutritionalValues) {
        return new Menu(menuRequest.date(), menuRequest.username(), menus, nutritionalValues);
    }
}
