package com.eliascanalesnieto.foodtracker.dto.out;

import java.util.Objects;

public record NutritionalInformationResponse(String id, String shortName, String name,
                                             String unit) implements Comparable<NutritionalInformationResponse> {

    @Override
    public int compareTo(final NutritionalInformationResponse other) {
        if(Objects.isNull(other) || Objects.isNull(other.shortName())) {
            return 1;
        }

        if(Objects.isNull(shortName)) {
            return -1;
        }

        return shortName.compareTo(other.shortName());
    }
}
