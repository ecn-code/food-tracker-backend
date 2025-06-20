package com.eliascanalesnieto.foodtracker.dto.out;

import java.util.List;

public record RecipeResponse(String id, String name, String description,
                             List<ProductValueResponse> products, List<NutritionalValueResponse> nutritionalValues) {
}
