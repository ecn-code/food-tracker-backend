package com.eliascanalesnieto.foodtracker.dto.in;

import java.util.List;

public record ProductRequest(String id, String name, String description,
                             String recipeId, List<NutritionalValueRequest> nutritionalValues) {
}
