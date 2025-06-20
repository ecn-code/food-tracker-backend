package com.eliascanalesnieto.foodtracker.dto.out;

import java.util.List;

public record PaginatedList<T>(List<T> items, String lastEvaluatedKey) {
}
