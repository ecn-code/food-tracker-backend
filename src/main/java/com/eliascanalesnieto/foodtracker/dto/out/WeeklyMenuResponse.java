package com.eliascanalesnieto.foodtracker.dto.out;

import java.util.List;

public record WeeklyMenuResponse(List<MenuResponse> menus, List<NutritionalValueResponse> nutritionalValues) {
}
