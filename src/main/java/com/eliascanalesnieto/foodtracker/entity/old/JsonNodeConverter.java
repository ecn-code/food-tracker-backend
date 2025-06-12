package com.eliascanalesnieto.foodtracker.entity.old;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import software.amazon.awssdk.enhanced.dynamodb.AttributeConverter;
import software.amazon.awssdk.enhanced.dynamodb.AttributeValueType;
import software.amazon.awssdk.enhanced.dynamodb.EnhancedType;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.Map;
import java.util.stream.Collectors;

public class JsonNodeConverter implements AttributeConverter<JsonNode> {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Override
    public AttributeValue transformFrom(JsonNode input) {
        if (input == null || input.isNull()) {
            return AttributeValue.builder().nul(true).build();
        }
        try {
            // Convertimos JsonNode a String JSON
            String jsonString = MAPPER.writeValueAsString(input);
            return AttributeValue.builder().s(jsonString).build();
        } catch (Exception e) {
            throw new RuntimeException("Error serializando JsonNode a String", e);
        }
    }

    @Override
    public JsonNode transformTo(AttributeValue input) {
        if (input == null || input.nul() != null && input.nul()) {
            return null;
        }
        try {
            // Convertimos String JSON a JsonNode
            Object javaObject = attributeValueToObject(input);
            return MAPPER.valueToTree(javaObject);
        } catch (Exception e) {
            throw new RuntimeException("Error deserializando String a JsonNode", e);
        }
    }

    @Override
    public EnhancedType<JsonNode> type() {
        return EnhancedType.of(JsonNode.class);
    }

    @Override
    public AttributeValueType attributeValueType() {
        return AttributeValueType.S;
    }

    private Object attributeValueToObject(AttributeValue av) {
        if (av == null) return null;
        if (av.s() != null) return av.s();
        if (av.n() != null) return av.n();
        if (av.bool() != null) return av.bool();
        if (av.m() != null && !av.m().isEmpty()) {
            return av.m().entrySet().stream()
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            e -> attributeValueToObject(e.getValue())
                    ));
        }
        if (av.l() != null) {
            return av.l().stream()
                    .map(this::attributeValueToObject)
                    .collect(Collectors.toList());
        }
        return null;
    }

}
