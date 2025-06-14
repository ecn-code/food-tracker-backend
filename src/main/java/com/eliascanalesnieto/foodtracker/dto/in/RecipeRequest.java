package com.eliascanalesnieto.foodtracker.dto.in;

import java.util.List;

public record RecipeRequest(String id, String name, String description,
                            List<ProductValueRequest> products) {
}
