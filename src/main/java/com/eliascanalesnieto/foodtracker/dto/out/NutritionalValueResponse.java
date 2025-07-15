package com.eliascanalesnieto.foodtracker.dto.out;

import java.util.Objects;

public record NutritionalValueResponse(String id, String name, String shortName, String unit, Double value) implements Comparable<NutritionalValueResponse> {

    @Override
    public int compareTo(final NutritionalValueResponse other) {
        if(Objects.isNull(other) || Objects.isNull(other.shortName())) {
            return 1;
        }

        if(Objects.isNull(shortName)) {
            return -1;
        }

        return shortName.compareTo(other.shortName());
    }

}
