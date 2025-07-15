package com.eliascanalesnieto.foodtracker.model;

import com.eliascanalesnieto.foodtracker.dto.in.ProductRequest;
import com.eliascanalesnieto.foodtracker.utils.IdFormat;
import org.springframework.util.StringUtils;

import java.util.List;

public record Product(String id, String name, String description, String recipeId, List<NutritionalValue> nutritionalValues) {

    public String id() {
        if (StringUtils.hasText(id)) {
            return id;
        }

        return IdFormat.createId();
    }

    public static Product build(final ProductRequest productRequest, final List<NutritionalValue> nutritionalValues) {
        return new Product(
                productRequest.id(),
                productRequest.name(),
                productRequest.description(),
                productRequest.recipeId(),
                nutritionalValues
        );
    }
}
