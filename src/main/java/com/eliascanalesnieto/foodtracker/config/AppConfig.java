package com.eliascanalesnieto.foodtracker.config;

public record AppConfig(String origin, CryptoConfig crypto, DynamoDBConfig dynamo) {
}