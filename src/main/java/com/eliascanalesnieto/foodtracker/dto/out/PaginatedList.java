package com.eliascanalesnieto.foodtracker.dto.out;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Map;

public record PaginatedList<T>(List<T> items,
                               @JsonProperty("last_evaluated_key") String lastEvaluatedKey) {

    private static final ObjectMapper mapper = new ObjectMapper();
    public static final String PK = "PK";
    public static final String SK = "SK";
    public static final String ORDER_BY = "order_by";

    public static String encodeLastEvaluatedKey(final Map<String, AttributeValue> key) {
        if (key == null || key.isEmpty()) return null;

        try {
            final String pk = key.get(PK).s();
            final String sk = key.get(SK).s();
            final String orderBy = key.get(ORDER_BY).s();
            Map<String, String> simpleMap = Map.of(PK, pk, SK, sk, ORDER_BY, orderBy);

            String json = mapper.writeValueAsString(simpleMap);
            return Base64.getUrlEncoder().encodeToString(json.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new RuntimeException("Error al codificar LastEvaluatedKey", e);
        }
    }

    public static Map<String, AttributeValue> decodeLastEvaluatedKey(final String token) {
        if (token == null || token.isEmpty()) return null;

        try {
            byte[] decoded = Base64.getUrlDecoder().decode(token);
            Map<String, String> simpleMap = mapper.readValue(decoded, new TypeReference<>() {});
            return Map.of(
                    PK, AttributeValue.fromS(simpleMap.get(PK)),
                    SK, AttributeValue.fromS(simpleMap.get(SK)),
                    ORDER_BY, AttributeValue.fromS(simpleMap.get(ORDER_BY))
            );
        } catch (Exception e) {
            throw new RuntimeException("Error al decodificar LastEvaluatedKey", e);
        }
    }
}
