package com.eliascanalesnieto.foodtracker.utils;

import lombok.experimental.UtilityClass;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@UtilityClass
public class DateFormat {

    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    public static Date parse(final String yyyyMMdd) throws ParseException {
        return sdf.parse(yyyyMMdd);
    }

    public static String format(final Date date) throws ParseException {
        return sdf.format(date);
    }

}
