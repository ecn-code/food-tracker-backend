package com.eliascanalesnieto.foodtracker.config;

public record AppConfig(CryptoConfig crypto, DynamoDBConfig dynamo) {
}