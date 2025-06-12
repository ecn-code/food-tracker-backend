package com.eliascanalesnieto.foodtracker.dto.out;

public record ProductValueResponse(String id, String name, String recipeId, String unit, Double value) {
}
