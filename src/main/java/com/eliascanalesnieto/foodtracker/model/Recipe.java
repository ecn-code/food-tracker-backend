package com.eliascanalesnieto.foodtracker.model;

import com.eliascanalesnieto.foodtracker.dto.in.RecipeRequest;
import com.eliascanalesnieto.foodtracker.utils.IdFormat;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.List;

public record Recipe(String id, String name, String description,
                     List<ItemValue> products, Collection<ItemValue> nutritionalValues) {

    public String id() {
        if (StringUtils.hasText(id)) {
            return id;
        }

        return IdFormat.createId();
    }

    public static Recipe build(final RecipeRequest recipeRequest, final Collection<ItemValue> nutritionalValues) {
        return new Recipe(recipeRequest.id(), recipeRequest.name(), recipeRequest.description(),
                ItemValue.buildFromProductValues(recipeRequest.products()), nutritionalValues);
    }
}
