package com.eliascanalesnieto.foodtracker.dto.out;

import java.util.List;

public record ProductResponse(String id, String name, String description,
                              String recipeId, List<ItemValueResponse> nutritionalValues) {
}
