package com.eliascanalesnieto.foodtracker.dto.in;

public record ProductValueRequest(String id, String name, String recipeId, String unit, Double value) {
}
