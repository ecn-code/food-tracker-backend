package com.eliascanalesnieto.foodtracker.dto.out;

public record ProductValueResponse(String id, String name, String description, String recipeId, String unit, Double value) {
}
