package com.eliascanalesnieto.foodtracker.model;

import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.List;
import java.util.Map;

public record Paginated<T>(List<T> items, Map<String, AttributeValue> lastEvaluatedKey) {
}
