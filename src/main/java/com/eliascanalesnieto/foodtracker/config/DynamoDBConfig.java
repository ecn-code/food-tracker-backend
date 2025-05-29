package com.eliascanalesnieto.foodtracker.config;

public record DynamoDBConfig(String endpoint, String oldTableName, String tableName) {
}
