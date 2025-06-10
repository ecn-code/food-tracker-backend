package com.eliascanalesnieto.foodtracker.utils;

import lombok.experimental.UtilityClass;

import java.util.UUID;

@UtilityClass
public class IdFormat {

    public static final String SEPARATOR = "=";

    public static String format(final String... ids) {
        return String.join(SEPARATOR, ids);
    }

    public static String createId() {
        return UUID.randomUUID().toString();
    }

}
