package com.eliascanalesnieto.foodtracker.dto.out;

import java.util.List;
import java.util.Map;

public record MenuResponse(String date, String username, Map<String, List<ItemValueResponse>> products,
                           List<ItemValueResponse> nutritionalValues) {
}
