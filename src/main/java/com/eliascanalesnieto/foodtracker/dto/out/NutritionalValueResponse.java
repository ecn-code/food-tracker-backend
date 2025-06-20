package com.eliascanalesnieto.foodtracker.dto.out;

public record NutritionalValueResponse(String id, String name, String shortName, String unit, Double value) {
}
