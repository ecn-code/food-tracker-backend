package com.eliascanalesnieto.foodtracker.dto.in;

public record NutritionalValueRequest(String id, String name, String shortName, String unit, Double value) {
}
